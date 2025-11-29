package com.quogle.lavarise.menu;

import com.quogle.lavarise.blockentity.DepotBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DepotMenu extends AbstractContainerMenu {

    private final DepotBlockEntity blockEntity;

    public DepotMenu(int containerId, Inventory playerInventory, DepotBlockEntity blockEntity) {
        super(ModMenuTypes.DEPOT_MENU.get(), containerId);
        this.blockEntity = blockEntity;

        // Add depot slots (4 slots)
        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(new IItemHandlerWrapper(blockEntity, i), i, 44 + i * 18, 20));
        }

        // Add player inventory
        addPlayerInventory(playerInventory);
    }

    public DepotMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.DEPOT_MENU.get(), containerId);
        // For client, just store null for the block entity
        this.blockEntity = null;
    }

    public static MenuType<? extends DepotMenu> get() {
        return ModMenuTypes.DEPOT_MENU.get();
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 50 + row * 18));
            }
        }
        // Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 108));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // implement shift-click later
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
