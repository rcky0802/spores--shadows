package moldmod.screen;

import moldmod.block.dehumidifier.DehumidifierMode;
import moldmod.block.dehumidifier.DehumidifierRedstoneMode;
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

public class DehumidifierScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public DehumidifierScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1), new ArrayPropertyDelegate(8));
    }

    public DehumidifierScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.DEHUMIDIFIER, syncId);
        checkSize(inventory, 1);
        checkDataCount(propertyDelegate, 8);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        inventory.onOpen(playerInventory.player);

        // Slot 0: Combustibile al centro (x: 80, y: 37)
        this.addSlot(new Slot(inventory, 0, 80, 37) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return AbstractFurnaceBlockEntity.canUseAsFuel(stack);
            }
        });

        // Inventario del giocatore (3 righe x 9 colonne)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Hotbar del giocatore (9 slot)
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        this.addProperties(propertyDelegate);
    }

    public int getEnergy() {
        return propertyDelegate.get(0);
    }

    public int getEnergyCapacity() {
        return propertyDelegate.get(1);
    }

    public int getWaterMb() {
        return propertyDelegate.get(2);
    }

    public int getCapacityMb() {
        return propertyDelegate.get(3);
    }

    public DehumidifierRedstoneMode getRedstoneMode() {
        int val = propertyDelegate.get(4);
        return DehumidifierRedstoneMode.values()[Math.min(Math.max(val, 0), DehumidifierRedstoneMode.values().length - 1)];
    }

    public moldmod.block.dehumidifier.DehumidifierStatus getMachineStatus() {
        int val = propertyDelegate.get(5);
        return moldmod.block.dehumidifier.DehumidifierStatus.values()[Math.min(Math.max(val, 0), moldmod.block.dehumidifier.DehumidifierStatus.values().length - 1)];
    }

    public int getEnergyCostPerTick() {
        return propertyDelegate.get(6);
    }

    public DehumidifierMode getMode() {
        int val = propertyDelegate.get(7);
        return DehumidifierMode.values()[Math.min(Math.max(val, 0), DehumidifierMode.values().length - 1)];
    }

    public int getWaterProgress(int pixelHeight) {
        int cap = this.getCapacityMb();
        if (cap <= 0) cap = 2000;
        return (int) Math.min(pixelHeight, ((long) this.getWaterMb() * pixelHeight) / cap);
    }

    public int getEnergyProgress(int pixelHeight) {
        int cap = this.getEnergyCapacity();
        if (cap <= 0) cap = 32000;
        return (int) Math.min(pixelHeight, ((long) this.getEnergy() * pixelHeight) / cap);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot == 0) {
                // Da slot carburante a inventario giocatore
                if (!this.insertItem(originalStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(originalStack, newStack);
            } else {
                // Da inventario a slot carburante
                if (AbstractFurnaceBlockEntity.canUseAsFuel(originalStack)) {
                    if (!this.insertItem(originalStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (invSlot >= 1 && invSlot < 28) {
                    if (!this.insertItem(originalStack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (invSlot >= 28 && invSlot < 37 && !this.insertItem(originalStack, 1, 28, false)) {
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
