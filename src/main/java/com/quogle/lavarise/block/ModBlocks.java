package com.quogle.lavarise.block;

import com.quogle.lavarise.LavaRise;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.registries.Registries;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlocks {

    // Create a DeferredRegister for blocks
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(LavaRise.MOD_ID);

    // Register the Depot block using DeferredHolder
    public static final DeferredBlock<DepotBlock> DEPOT_BLOCK =
            BLOCKS.register("depot", () -> new DepotBlock(
                    BlockBehaviour.Properties.of().strength(3.5f).noOcclusion()
            ));
}
