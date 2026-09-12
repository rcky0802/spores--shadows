package moldmod.block.purifier;

import me.shedaniel.autoconfig.AutoConfig;
import moldmod.atmosphere.RoomAtmosphereCalculator;
import moldmod.block.entity.ModBlockEntities;
import moldmod.config.ModConfig;
import moldmod.item.ModItems;
import moldmod.screen.AirPurifierScreenHandler;
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
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.List;

public class AirPurifierBlockEntity extends BlockEntity implements SidedInventory, NamedScreenHandlerFactory {

    // Slot 0: Combustibile solido, Slot 1: Filtri Antispore
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    private int filterWearTicks = 0;
    private int energy = 0;
    private PurifierRedstoneMode redstoneMode = PurifierRedstoneMode.IGNORED;

    // Caching periodico della stanza (ogni 20 tick / 1s) per ottimizzare la CPU del server
    private int atmosphereCheckCounter = 0;
    private double cachedMraw = 0.0;
    private double cachedMeff = 0.0;
    private boolean cachedIsLethal = false;

    // Buffer energetico TR Energy / RF (32.000 E, max insert 500 E/t, extract 0)
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

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> getEnergy();
                case 1 -> getEnergyCapacity();
                case 2 -> filterWearTicks;
                case 3 -> getFilterDurabilityTicks();
                case 4 -> redstoneMode.ordinal();
                case 5 -> getStatus().ordinal();
                case 6 -> inventory.get(0).getCount();
                case 7 -> inventory.get(1).getCount();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> setEnergy(value);
                case 2 -> filterWearTicks = value;
                case 4 -> redstoneMode = PurifierRedstoneMode.values()[MathHelper.clamp(value, 0, PurifierRedstoneMode.values().length - 1)];
            }
        }

        @Override
        public int size() {
            return 8;
        }
    };

    public AirPurifierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AIR_PURIFIER, pos, state);
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getEnergy() {
        return (int) energyStorage.amount;
    }

    public void setEnergy(int value) {
        this.energy = MathHelper.clamp(value, 0, getEnergyCapacity());
        this.energyStorage.amount = this.energy;
        markDirty();
    }

    public int getFilterWearTicks() {
        return filterWearTicks;
    }

    public void setFilterWearTicks(int ticks) {
        this.filterWearTicks = Math.max(0, ticks);
        markDirty();
    }

    public int getFilterCount() {
        return inventory.get(1).getCount();
    }

    public PurifierRedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(PurifierRedstoneMode mode) {
        this.redstoneMode = mode;
        markDirty();
        if (world != null && !world.isClient) {
            syncBlockState(world, pos, getCachedState());
            world.updateComparators(pos, getCachedState().getBlock());
        }
    }

    private ModConfig getConfig() {
        try {
            return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        } catch (Exception e) {
            return null;
        }
    }

    public int getEnergyCapacity() {
        ModConfig config = getConfig();
        return (config != null && config.airPurifier != null) ? config.airPurifier.energy_capacity : 32000;
    }

    public int getEnergyCostPerTick() {
        ModConfig config = getConfig();
        return (config != null && config.airPurifier != null) ? config.airPurifier.energy_cost_per_tick : 10;
    }

    public float getFuelMultiplier() {
        ModConfig config = getConfig();
        return (config != null && config.airPurifier != null) ? config.airPurifier.fuel_multiplier : 4.0f;
    }

    public int getFilterDurabilityTicks() {
        ModConfig config = getConfig();
        return (config != null && config.airPurifier != null) ? config.airPurifier.filter_durability_ticks : 2400;
    }

    public double getPurifierCleaningPower() {
        ModConfig config = getConfig();
        return (config != null && config.airPurifier != null) ? config.airPurifier.purifier_cleaning_power : 48.0;
    }

    public PurifierStatus getStatus() {
        if (world != null) {
            BlockState state = getCachedState();
            if (state.contains(AirPurifierBlock.STATUS)) {
                return state.get(AirPurifierBlock.STATUS);
            }
        }
        if (energy < getEnergyCostPerTick()) {
            return PurifierStatus.OFF;
        }
        if (filterWearTicks <= 0 && (inventory.get(1).isEmpty() || !inventory.get(1).isOf(ModItems.SPORE_FILTER))) {
            return PurifierStatus.FILTER_DEPLETED;
        }
        return PurifierStatus.RUNNING;
    }

    public static int calculateFilterLevel(int activeTicks, int maxTicks, int backupCount) {
        if (activeTicks <= 0 && backupCount <= 0) {
            return 0;
        }
        if (backupCount > 0) {
            return 4;
        }
        if (maxTicks <= 0) {
            return 0;
        }
        double fraction = (double) activeTicks / (double) maxTicks;
        if (fraction > 0.75) return 4;
        if (fraction > 0.50) return 3;
        if (fraction > 0.25) return 2;
        if (fraction > 0.00) return 1;
        return 0;
    }

    public int getComparatorOutput() {
        int backupCount = inventory.get(1).getCount();
        int maxWear = getFilterDurabilityTicks();
        double activeVal = (maxWear > 0 && filterWearTicks > 0) ? ((double) filterWearTicks / (double) maxWear) : 0.0;
        double totalItems = backupCount + activeVal;
        if (totalItems <= 0.0) {
            return 0;
        }
        return MathHelper.clamp((int) Math.ceil((totalItems / 64.0) * 15.0), 1, 15);
    }

    private void updateBlockState(World world, BlockPos pos, BlockState state, PurifierStatus newStatus, int newFilterLevel) {
        boolean statusChanged = !state.contains(AirPurifierBlock.STATUS) || state.get(AirPurifierBlock.STATUS) != newStatus;
        boolean levelChanged = !state.contains(AirPurifierBlock.FILTER_LEVEL) || state.get(AirPurifierBlock.FILTER_LEVEL) != newFilterLevel;

        if (statusChanged || levelChanged) {
            BlockState updated = state.with(AirPurifierBlock.STATUS, newStatus)
                    .with(AirPurifierBlock.FILTER_LEVEL, newFilterLevel);
            world.setBlockState(pos, updated, 3);
            markDirty();
        }
    }

    public void syncBlockState(World world, BlockPos pos, BlockState state) {
        int filterLevel = calculateFilterLevel(this.filterWearTicks, getFilterDurabilityTicks(), inventory.get(1).getCount());
        updateBlockState(world, pos, state, getStatus(), filterLevel);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;

        // 1. Campionamento periodico dell'atmosfera (ogni 20 tick / 1s)
        atmosphereCheckCounter++;
        if (atmosphereCheckCounter >= 20 || cachedMraw < 0.0) {
            atmosphereCheckCounter = 0;
            try {
                var miasma = RoomAtmosphereCalculator.calculateMiasma(world, pos);
                if (miasma != null) {
                    cachedMraw = miasma.toxicScore;
                    cachedMeff = miasma.netMiasma;
                    cachedIsLethal = (miasma.level == RoomAtmosphereCalculator.AirToxicityLevel.LETHAL_POISON);
                } else {
                    cachedMraw = 0.0;
                    cachedMeff = 0.0;
                    cachedIsLethal = false;
                }
            } catch (Exception e) {
                cachedMraw = 0.0;
                cachedMeff = 0.0;
                cachedIsLethal = false;
            }
        }

        int maxWear = getFilterDurabilityTicks();
        ItemStack filterSlot = inventory.get(1);
        int backupCount = filterSlot.getCount();
        int filterLevel = calculateFilterLevel(this.filterWearTicks, maxWear, backupCount);

        // 2. Controllo Redstone
        int redstonePower = world.getReceivedRedstonePower(pos);
        boolean redstoneAllows = switch (redstoneMode) {
            case IGNORED -> true;
            case LOW -> (redstonePower == 0);
            case HIGH -> (redstonePower > 0);
        };

        if (!redstoneAllows) {
            updateBlockState(world, pos, state, PurifierStatus.OFF, filterLevel);
            return;
        }

        // 3. Conversione istantanea combustibile solido in buffer TR Energy interno
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

        // 4. Controllo Alimentazione Elettrica (Priorità massima sull'assenza del filtro)
        int cost = getEnergyCostPerTick();
        if (energy < cost) {
            // Privo di energia/combustibile: stato OFF prioritario
            updateBlockState(world, pos, state, PurifierStatus.OFF, filterLevel);
            return;
        }

        // 5. Caricamento o reintegro iniziale del filtro dallo slot
        if (this.filterWearTicks <= 0) {
            if (!filterSlot.isEmpty() && filterSlot.isOf(ModItems.SPORE_FILTER)) {
                filterSlot.decrement(1);
                this.filterWearTicks = maxWear;
                filterLevel = calculateFilterLevel(this.filterWearTicks, maxWear, filterSlot.getCount());
                markDirty();
                world.updateComparators(pos, state.getBlock());
            } else {
                // Macchina alimentata ma privo di filtro: stato FILTER_DEPLETED
                updateBlockState(world, pos, state, PurifierStatus.FILTER_DEPLETED, 0);
                return;
            }
        }

        // 6. Consumo Operativo & Purificazione Attiva
        energy -= cost;
        energyStorage.amount = energy;

        // Usura progressiva del filtro: 2 pt/tick sotto miasma letale, 1 pt/tick base
        int wear = cachedIsLethal ? 2 : 1;
        this.filterWearTicks = Math.max(0, this.filterWearTicks - wear);

        // Se il filtro attivo si è appena consumato:
        if (this.filterWearTicks == 0) {
            if (!filterSlot.isEmpty() && filterSlot.isOf(ModItems.SPORE_FILTER)) {
                filterSlot.decrement(1);
                this.filterWearTicks = maxWear;
                markDirty();
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0f, 1.2f);
            }
            world.updateComparators(pos, state.getBlock());
        }

        filterLevel = calculateFilterLevel(this.filterWearTicks, maxWear, inventory.get(1).getCount());
        PurifierStatus newStatus = (this.filterWearTicks > 0) ? PurifierStatus.RUNNING : PurifierStatus.FILTER_DEPLETED;
        updateBlockState(world, pos, state, newStatus, filterLevel);

        // 8. Trigger Avanzamento: Aria Pura nel Sottosuolo (Y <= 0 e Meff == 0.0 in presenza di muffa sanificata)
        if ((world.getTime() % 20 == 0) && pos.getY() <= 0 && cachedMeff <= 0.0 && cachedMraw > 0.0) {
            Box checkArea = new Box(pos).expand(16);
            List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, checkArea, p -> true);
            for (PlayerEntity p : players) {
                moldmod.block.MoldyBlockHelper.grantAdvancement(p, "pure_air_depths");
            }
        }
    }

    // --- SidedInventory Implementation ---

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
            if (world != null && !world.isClient) {
                syncBlockState(world, pos, getCachedState());
                world.updateComparators(pos, getCachedState().getBlock());
            }
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(inventory, slot);
        if (!result.isEmpty()) {
            markDirty();
            if (world != null && !world.isClient) {
                syncBlockState(world, pos, getCachedState());
                world.updateComparators(pos, getCachedState().getBlock());
            }
        }
        return result;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
        if (world != null && !world.isClient) {
            syncBlockState(world, pos, getCachedState());
            world.updateComparators(pos, getCachedState().getBlock());
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        inventory.clear();
        markDirty();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            return new int[]{1}; // Inserimento filtri dall'alto
        }
        return new int[]{0}; // Inserimento/estrazione carburante e recipienti da lati e fondo
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (dir != null) {
            if (dir == Direction.UP && slot != 1) {
                return false;
            }
            if (dir != Direction.UP && slot != 0) {
                return false;
            }
        }
        if (slot == 0) {
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
        }
        if (slot == 1) {
            return stack.isOf(ModItems.SPORE_FILTER);
        }
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (slot == 0 && stack.isOf(Items.BUCKET)) {
            return true; // Consente alle tramogge di prelevare secchi vuoti residui di lava
        }
        return false;
    }

    // --- NamedScreenHandlerFactory Implementation ---

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.spores--shadows.air_purifier");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AirPurifierScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    // --- NBT Serialization ---

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
        nbt.putInt("Energy", getEnergy());
        nbt.putInt("FilterWearTicks", this.filterWearTicks);
        nbt.putString("RedstoneMode", this.redstoneMode.asString());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory, registryLookup);
        int savedEnergy = nbt.getInt("Energy");
        setEnergy(savedEnergy);
        this.filterWearTicks = nbt.getInt("FilterWearTicks");
        if (nbt.contains("RedstoneMode")) {
            String modeStr = nbt.getString("RedstoneMode");
            for (PurifierRedstoneMode mode : PurifierRedstoneMode.values()) {
                if (mode.asString().equalsIgnoreCase(modeStr)) {
                    this.redstoneMode = mode;
                    break;
                }
            }
        }
    }
}
