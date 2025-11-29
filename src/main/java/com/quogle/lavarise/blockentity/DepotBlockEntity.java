package com.quogle.lavarise.blockentity;

import com.quogle.lavarise.menu.DepotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import java.util.Map;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;


public class DepotBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler stacks = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // slot 0 can accept anything if empty
            if (slot == 0) {
                Item item = getDepotItemType();
                return item == Items.AIR || stack.getItem() == item;
            }

            // other slots accept only depot item type
            Item item = getDepotItemType();
            return item != Items.AIR && stack.getItem() == item;
        }
    };

    public DepotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DEPOT.get(), pos, state);

    }


    // Get the item this depot stores
    public Item getDepotItemType() {
        ItemStack base = stacks.getStackInSlot(0);
        return base.isEmpty() ? Items.AIR : base.getItem();
    }

    // Total number of stored items (0–256 normally)
    public int getTotalCount() {
        int total = 0;
        for (int i = 0; i < 4; i++) {
            total += stacks.getStackInSlot(i).getCount();
        }
        return total;
    }

    // Return combined stacks (for global manager)
    public Map<Item, Integer> exportTotals() {
        Item item = getDepotItemType();
        if (item == Items.AIR) return Map.of();
        return Map.of(item, getTotalCount());
    }

    // Add items programmatically (used when the player puts items IN)
    public ItemStack insert(ItemStack incoming, boolean simulate) {
        if (incoming.isEmpty()) return ItemStack.EMPTY;

        Item itemType = getDepotItemType();
        if (itemType != Items.AIR && incoming.getItem() != itemType) {
            return incoming; // reject
        }

        // If empty, establish type by using slot 0
        if (itemType == Items.AIR) {
            stacks.insertItem(0, incoming.copy(), simulate);
            incoming.shrink(incoming.getCount() - stacks.getStackInSlot(0).getCount());
            itemType = incoming.getItem();
        }

        ItemStack remaining = incoming.copy();
        for (int i = 0; i < 4; i++) {
            remaining = stacks.insertItem(i, remaining, simulate);
            if (remaining.isEmpty()) break;
        }
        return remaining;
    }

    // Extract N items
    public ItemStack extract(int amount, boolean simulate) {
        Item itemType = getDepotItemType();
        if (itemType == Items.AIR) return ItemStack.EMPTY;

        int toTake = amount;
        ItemStack result = new ItemStack(itemType, 0);

        for (int i = 0; i < 4 && toTake > 0; i++) {
            ItemStack slot = stacks.getStackInSlot(i);
            if (slot.isEmpty()) continue;

            int take = Math.min(toTake, slot.getCount());
            if (!simulate) {
                slot.shrink(take);
                stacks.setStackInSlot(i, slot);
            }
            result.grow(take);
            toTake -= take;
        }

        if (getTotalCount() == 0) {
            // reset type
            stacks.setStackInSlot(0, ItemStack.EMPTY);
        }

        return result;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("Depot");
    }
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new DepotMenu(id, playerInventory, this);
    }

}

