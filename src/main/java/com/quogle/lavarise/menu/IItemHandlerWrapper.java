package com.quogle.lavarise.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.quogle.lavarise.blockentity.DepotBlockEntity;

public class IItemHandlerWrapper implements Container {

    private final DepotBlockEntity blockEntity;
    private final int slotIndex;

    public IItemHandlerWrapper(DepotBlockEntity blockEntity, int slotIndex) {
        this.blockEntity = blockEntity;
        this.slotIndex = slotIndex;
    }

    @Override
    public int getContainerSize() { return 1; }

    @Override
    public boolean isEmpty() { return getItem(0).isEmpty(); }

    @Override
    public @NotNull ItemStack getItem(int i) { return blockEntity.stacks.getStackInSlot(slotIndex); }

    @Override
    public @NotNull ItemStack removeItem(int i, int count) {
        ItemStack stack = blockEntity.stacks.extractItem(slotIndex, count, false);
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        ItemStack stack = getItem(0).copy();
        blockEntity.stacks.setStackInSlot(slotIndex, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int i, @NotNull ItemStack stack) {
        blockEntity.stacks.setStackInSlot(slotIndex, stack);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() { setItem(0, ItemStack.EMPTY); }
}
