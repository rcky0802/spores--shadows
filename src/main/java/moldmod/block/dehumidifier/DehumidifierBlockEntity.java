package moldmod.block.dehumidifier;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.block.entity.ModBlockEntities;
import moldmod.config.ModConfig;
import moldmod.screen.DehumidifierScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class DehumidifierBlockEntity extends BlockEntity implements SidedInventory, NamedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private int burnTime = 0;
    private int maxBurnTime = 0;
    private int waterMb = 0;
    private int condensationTickCounter = 0;
    private int energy = 0;

    private DehumidifierRedstoneMode redstoneMode = DehumidifierRedstoneMode.IGNORED;
    private DehumidifierMode mode = DehumidifierMode.DEHUMIDIFY;

    // Energy storage per TeamReborn Energy API (Compatibilità RF / FE, buffer 32.000, max insert 500 E/t, extract 0)
    private final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(32000, 500, 0) {
        @Override
        public long getCapacity() {
            return getEnergyCapacity();
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            long maxInsert = 500;
            long capacity = getEnergyCapacity();
            long inserted = Math.min(maxInsert, Math.min(maxAmount, capacity - amount));
            if (inserted > 0) {
                updateSnapshots(transaction);
                amount += inserted;
                return inserted;
            }
            return 0;
        }

        @Override
        protected void onFinalCommit() {
            energy = (int) amount;
            markDirty();
        }
    };

    // Fluid storage per Fabric Transfer API
    private final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return (long) getCapacityMb() * (FluidConstants.BUCKET / 1000L); // 162.000 droplets
        }

        @Override
        protected boolean canInsert(FluidVariant variant) {
            // Accetta inserimento esclusivamente di acqua pura quando è in modalità HUMIDIFY (nebulizzazione)
            return variant.getFluid() == net.minecraft.fluid.Fluids.WATER && mode == DehumidifierMode.HUMIDIFY;
        }

        @Override
        protected boolean canExtract(FluidVariant variant) {
            // I tubi funzionano in output/estrazione quando deve espellere condensa (DEHUMIDIFY)
            return mode == DehumidifierMode.DEHUMIDIFY;
        }

        @Override
        protected void onFinalCommit() {
            waterMb = (int) (amount / (FluidConstants.BUCKET / 1000L));
            markDirty();
            if (world != null && !world.isClient) {
                updateBlockState(world, pos, getCachedState(), getStatus(), waterMb, mode);
                world.updateComparators(pos, getCachedState().getBlock());
            }
        }
    };

    // PropertyDelegate per sincronizzare i valori con lo ScreenHandler (energia, cap. energia, acqua, cap. acqua, redstone, stato, consumo, modalità)
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) energyStorage.amount;
                case 1 -> getEnergyCapacity();
                case 2 -> waterMb;
                case 3 -> getCapacityMb();
                case 4 -> redstoneMode.ordinal();
                case 5 -> getStatus().ordinal();
                case 6 -> getEnergyCostPerTick();
                case 7 -> mode.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> setEnergy(value);
                case 2 -> waterMb = value;
                case 4 -> redstoneMode = DehumidifierRedstoneMode.values()[MathHelper.clamp(value, 0, 2)];
                case 7 -> mode = DehumidifierMode.values()[MathHelper.clamp(value, 0, 1)];
            }
        }

        @Override
        public int size() {
            return 8;
        }
    };

    public DehumidifierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DEHUMIDIFIER, pos, state);
    }

    public SingleVariantStorage<FluidVariant> getFluidStorage() {
        return fluidStorage;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getWaterMb() {
        return waterMb;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getEnergy() {
        return (int) energyStorage.amount;
    }

    public int drainWater(int amount) {
        int drained = Math.min(waterMb, amount);
        if (drained > 0) {
            waterMb -= drained;
            syncFluidStorage();
            markDirty();
            if (world != null && !world.isClient) {
                updateBlockState(world, pos, getCachedState(), getStatus(), waterMb, mode);
                world.updateComparators(pos, getCachedState().getBlock());
            }
        }
        return drained;
    }

    public int addWater(int amount) {
        int capacity = getCapacityMb();
        int added = Math.min(amount, capacity - waterMb);
        if (added > 0) {
            waterMb += added;
            syncFluidStorage();
            markDirty();
            if (world != null && !world.isClient) {
                updateBlockState(world, pos, getCachedState(), getStatus(), waterMb, mode);
                world.updateComparators(pos, getCachedState().getBlock());
            }
        }
        return added;
    }

    private void syncFluidStorage() {
        if (waterMb > 0) {
            fluidStorage.variant = FluidVariant.of(net.minecraft.fluid.Fluids.WATER);
            fluidStorage.amount = (long) waterMb * (FluidConstants.BUCKET / 1000L);
        } else {
            fluidStorage.variant = FluidVariant.blank();
            fluidStorage.amount = 0;
        }
    }

    private ModConfig getConfig() {
        try {
            return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception e) {
            return null;
        }
    }

    public int getCapacityMb() {
        ModConfig config = getConfig();
        return (config != null && config.dehumidifier != null) ? config.dehumidifier.capacity_mb : 2000;
    }

    public int getEnergyCapacity() {
        ModConfig config = getConfig();
        return (config != null && config.dehumidifier != null) ? config.dehumidifier.energy_capacity : 32000;
    }

    public int getEnergyCostPerTick() {
        ModConfig config = getConfig();
        return (config != null && config.dehumidifier != null) ? config.dehumidifier.energy_cost_per_tick : 10;
    }

    public float getFuelMultiplier() {
        ModConfig config = getConfig();
        return (config != null && config.dehumidifier != null) ? config.dehumidifier.fuel_multiplier : 1.0f;
    }

    public int getTicksPerMb() {
        ModConfig config = getConfig();
        return (config != null && config.dehumidifier != null) ? config.dehumidifier.ticks_per_mb : 24;
    }

    public DehumidifierRedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(DehumidifierRedstoneMode mode) {
        this.redstoneMode = mode;
        markDirty();
    }

    public DehumidifierMode getMode() {
        return mode;
    }

    public void setMode(DehumidifierMode newMode) {
        if (this.mode != newMode) {
            this.mode = newMode;
            markDirty();
            if (world != null && !world.isClient) {
                updateBlockState(world, pos, getCachedState(), getStatus(), waterMb, mode);
                world.updateComparators(pos, getCachedState().getBlock());
            }
        }
    }

    public void setWaterMb(int waterMb) {
        this.waterMb = MathHelper.clamp(waterMb, 0, getCapacityMb());
        syncFluidStorage();
        markDirty();
    }

    public void setEnergy(int energy) {
        this.energy = MathHelper.clamp(energy, 0, getEnergyCapacity());
        this.energyStorage.amount = this.energy;
        markDirty();
    }

    public int getComparatorOutput() {
        int capacity = getCapacityMb();
        if (capacity <= 0) return 0;
        return MathHelper.floor(((float) waterMb / (float) capacity) * 15.0f);
    }

    public DehumidifierStatus getStatus() {
        if (mode == DehumidifierMode.DEHUMIDIFY && waterMb >= getCapacityMb()) {
            return DehumidifierStatus.FULL;
        }
        if (mode == DehumidifierMode.HUMIDIFY && waterMb <= 0) {
            return DehumidifierStatus.OFF;
        }
        if (world != null) {
            int redstonePower = world.getReceivedRedstonePower(pos);
            boolean redstoneAllows = switch (redstoneMode) {
                case IGNORED -> true;
                case LOW -> (redstonePower == 0);
                case HIGH -> (redstonePower > 0);
            };
            if (!redstoneAllows) {
                return DehumidifierStatus.OFF;
            }
        }
        if (energy >= getEnergyCostPerTick()) {
            return DehumidifierStatus.RUNNING;
        }
        return DehumidifierStatus.OFF;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        boolean wasRunning = (state.contains(DehumidifierBlock.STATUS) && state.get(DehumidifierBlock.STATUS) == DehumidifierStatus.RUNNING);
        int capacity = getCapacityMb();

        // 1. Controllo serbatoio
        if (mode == DehumidifierMode.DEHUMIDIFY && waterMb >= capacity) {
            updateBlockState(world, pos, state, DehumidifierStatus.FULL, waterMb, mode);
            return;
        }
        if (mode == DehumidifierMode.HUMIDIFY && waterMb <= 0) {
            updateBlockState(world, pos, state, DehumidifierStatus.OFF, waterMb, mode);
            return;
        }

        // 2. Controllo Redstone Mode
        int redstonePower = world.getReceivedRedstonePower(pos);
        boolean redstoneAllows = switch (redstoneMode) {
            case IGNORED -> true;
            case LOW -> (redstonePower == 0);
            case HIGH -> (redstonePower > 0);
        };

        if (!redstoneAllows) {
            updateBlockState(world, pos, state, DehumidifierStatus.OFF, waterMb, mode);
            return;
        }

        // 3. Controllo umidità stanza circostante:
        double rawHumidity = 0.5;
        double currentHumidity = 0.5;
        try {
            var miasma = RoomAtmosphereCalculator.calculateMiasma(world, pos);
            if (miasma != null) {
                rawHumidity = miasma.rawHumidity;
                currentHumidity = miasma.currentHumidity;
            }
        } catch (Exception ignored) {}

        if (mode == DehumidifierMode.DEHUMIDIFY && rawHumidity <= 0.05) {
            updateBlockState(world, pos, state, DehumidifierStatus.OFF, waterMb, mode);
            return;
        }
        if (mode == DehumidifierMode.HUMIDIFY && currentHumidity >= 0.98) {
            updateBlockState(world, pos, state, DehumidifierStatus.OFF, waterMb, mode);
            return;
        }

        // 4. Conversione istantanea combustibile solido -> Elettricità interna
        int maxEnergy = getEnergyCapacity();
        ItemStack fuelStack = inventory.get(0);
        if (!fuelStack.isEmpty() && AbstractFurnaceBlockEntity.canUseAsFuel(fuelStack)) {
            int vanillaFuelTicks = AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(fuelStack.getItem(), 0);
            if (vanillaFuelTicks > 0) {
                int energyPerItem = Math.round(vanillaFuelTicks * getEnergyCostPerTick() * getFuelMultiplier());
                if (energy <= maxEnergy - energyPerItem || energy == 0) {
                    int energyToAdd = Math.min(energyPerItem, maxEnergy - energy);
                    energy += energyToAdd;
                    energyStorage.amount = energy;

                    Item remainder = fuelStack.getItem().getRecipeRemainder();
                    fuelStack.decrement(1);
                    if (fuelStack.isEmpty()) {
                        if (remainder != null) {
                            inventory.set(0, new ItemStack(remainder));
                        }
                    } else if (remainder != null) {
                        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(remainder));
                    }
                    markDirty();
                }
            }
        }

        // 5. Consumo Energetico & Deumidificazione/Umidificazione
        boolean isRunning = false;
        int energyCost = getEnergyCostPerTick();

        if (energy >= energyCost) {
            energy -= energyCost;
            energyStorage.amount = energy;
            isRunning = true;
        }

        // 6. Condensazione o Vaporizzazione
        if (isRunning) {
            condensationTickCounter++;
            int ticksPerMb = getTicksPerMb();

            if (mode == DehumidifierMode.DEHUMIDIFY) {
                int threshold = Math.max(1, (int) Math.round(ticksPerMb * (1.0 - Math.min(0.8, rawHumidity * 0.5))));
                if (condensationTickCounter >= threshold) {
                    condensationTickCounter = 0;
                    waterMb = Math.min(capacity, waterMb + 1);
                    syncFluidStorage();
                    world.updateComparators(pos, state.getBlock());
                }

                // Trigger advancement: Dry Oasis (Y <= 40, umidità effettiva currentHumidity < 0.15)
                if ((world.getTime() % 20 == 0) && pos.getY() <= 40 && currentHumidity < 0.15) {
                    Box checkArea = new Box(pos).expand(16);
                    List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, checkArea, p -> true);
                    for (PlayerEntity p : players) {
                        moldmod.block.MoldyBlockHelper.grantAdvancement(p, "dry_oasis");
                    }
                }
            } else {
                // Modalità Umidificazione: consuma acqua dal serbatoio per nebulizzare
                int threshold = Math.max(1, ticksPerMb);
                if (condensationTickCounter >= threshold) {
                    condensationTickCounter = 0;
                    waterMb = Math.max(0, waterMb - 1);
                    syncFluidStorage();
                    world.updateComparators(pos, state.getBlock());
                }
            }
        }

        DehumidifierStatus newStatus = (mode == DehumidifierMode.DEHUMIDIFY && waterMb >= capacity)
                ? DehumidifierStatus.FULL
                : (isRunning ? DehumidifierStatus.RUNNING : DehumidifierStatus.OFF);
        updateBlockState(world, pos, state, newStatus, waterMb, mode);

        if (wasRunning != isRunning) {
            markDirty();
        }
    }

    private void updateBlockState(World world, BlockPos pos, BlockState state, DehumidifierStatus newStatus, int currentWater, DehumidifierMode currentMode) {
        int capacity = getCapacityMb();
        int waterLevel = (currentWater <= 0) ? 0 : MathHelper.clamp(1 + (currentWater - 1) * 4 / Math.max(1, capacity), 0, 4);

        if (!state.contains(DehumidifierBlock.STATUS) || !state.contains(DehumidifierBlock.WATER_LEVEL) || !state.contains(DehumidifierBlock.MODE)
                || state.get(DehumidifierBlock.STATUS) != newStatus
                || state.get(DehumidifierBlock.WATER_LEVEL) != waterLevel
                || state.get(DehumidifierBlock.MODE) != currentMode) {
            world.setBlockState(pos, state.with(DehumidifierBlock.STATUS, newStatus)
                    .with(DehumidifierBlock.WATER_LEVEL, waterLevel)
                    .with(DehumidifierBlock.MODE, currentMode), 3);
        }
    }

    // --- SidedInventory ---

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0}; // Omnidirezionale da tutte le 6 facce
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction dir) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(stack); // Accetta carburanti da tutti i lati
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false; // Solo input, nessun item di output
    }

    // --- NBT Serialization ---

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
        nbt.putInt("BurnTime", burnTime);
        nbt.putInt("MaxBurnTime", maxBurnTime);
        nbt.putInt("WaterMb", waterMb);
        nbt.putInt("Energy", energy);
        nbt.putString("RedstoneMode", redstoneMode.asString());
        nbt.putString("Mode", mode.asString());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        inventory.clear();
        Inventories.readNbt(nbt, inventory, registryLookup);
        burnTime = nbt.getInt("BurnTime");
        maxBurnTime = nbt.getInt("MaxBurnTime");
        waterMb = nbt.getInt("WaterMb");
        energy = nbt.getInt("Energy");
        energyStorage.amount = energy;
        if (nbt.contains("RedstoneMode")) {
            String modeStr = nbt.getString("RedstoneMode");
            for (DehumidifierRedstoneMode m : DehumidifierRedstoneMode.values()) {
                if (m.asString().equalsIgnoreCase(modeStr)) {
                    this.redstoneMode = m;
                    break;
                }
            }
        }
        if (nbt.contains("Mode")) {
            String modeStr = nbt.getString("Mode");
            for (DehumidifierMode m : DehumidifierMode.values()) {
                if (m.asString().equalsIgnoreCase(modeStr)) {
                    this.mode = m;
                    break;
                }
            }
        }
        syncFluidStorage();
    }

    // --- NamedScreenHandlerFactory ---

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.spores--shadows.dehumidifier");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new DehumidifierScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
