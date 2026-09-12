package moldmod.screen;

import moldmod.block.purifier.PurifierRedstoneMode;
import moldmod.block.purifier.PurifierStatus;
import moldmod.item.ModItems;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class AirPurifierScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public AirPurifierScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(8));
    }

    public AirPurifierScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.AIR_PURIFIER, syncId);
        checkSize(inventory, 2);
        checkDataCount(propertyDelegate, 8);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        inventory.onOpen(playerInventory.player);

        // Slot 0: Combustibile solido (x: 62, y: 37)
        this.addSlot(new Slot(inventory, 0, 62, 37) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
            }
        });

        // Slot 1: Filtro Antispore (x: 98, y: 37)
        this.addSlot(new Slot(inventory, 1, 98, 37) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.SPORE_FILTER);
            }
        });

        // Inventario del giocatore (3 righe x 9 colonne, y: 84)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar del giocatore (9 slot, y: 142)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addProperties(propertyDelegate);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getEnergy() {
        return propertyDelegate.get(0);
    }

    public int getEnergyCapacity() {
        return propertyDelegate.get(1);
    }

    public int getFilterWearTicks() {
        return propertyDelegate.get(2);
    }

    public int getFilterDurabilityTicks() {
        return propertyDelegate.get(3);
    }

    public PurifierRedstoneMode getRedstoneMode() {
        int val = propertyDelegate.get(4);
        return PurifierRedstoneMode.values()[Math.min(Math.max(val, 0), PurifierRedstoneMode.values().length - 1)];
    }

    public PurifierStatus getMachineStatus() {
        int val = propertyDelegate.get(5);
        return PurifierStatus.values()[Math.min(Math.max(val, 0), PurifierStatus.values().length - 1)];
    }

    public int getFuelCount() {
        return propertyDelegate.get(6);
    }

    public int getFilterCount() {
        return propertyDelegate.get(7);
    }

    public boolean isConsumingEnergy() {
        return getMachineStatus() == PurifierStatus.RUNNING;
    }

    public int getEnergyProgress(int height) {
        int energy = getEnergy();
        int capacity = getEnergyCapacity();
        if (capacity <= 0) return 0;
        return (int) Math.round(((double) energy / capacity) * height);
    }

    public int getFilterProgress(int height) {
        int wear = getFilterWearTicks();
        int maxWear = getFilterDurabilityTicks();
        if (maxWear <= 0) return 0;
        return (int) Math.round(((double) wear / maxWear) * height);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            // Dagli slot della macchina (0 o 1) verso l'inventario del player (slot 2..37)
            if (invSlot == 0 || invSlot == 1) {
                if (!this.insertItem(originalStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(originalStack, newStack);
            } else {
                // Dall'inventario del player verso la macchina:
                // Se è un Filtro Antispore -> priorità assoluta allo Slot 1
                if (originalStack.isOf(ModItems.SPORE_FILTER)) {
                    if (!this.insertItem(originalStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Se è un combustibile solido valido -> priorità allo Slot 0
                else if (AbstractFurnaceBlockEntity.canUseAsFuel(originalStack)) {
                    if (!this.insertItem(originalStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Spostamenti standard tra inventario principale e hotbar
                else if (invSlot >= 2 && invSlot < 29) {
                    if (!this.insertItem(originalStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (invSlot >= 29 && invSlot < 38) {
                    if (!this.insertItem(originalStack, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(originalStack, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (originalStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, originalStack);
        }

        return newStack;
    }
}
