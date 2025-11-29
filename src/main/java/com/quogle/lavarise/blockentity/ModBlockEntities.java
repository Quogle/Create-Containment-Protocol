package com.quogle.lavarise.blockentity;

import com.quogle.lavarise.LavaRise;
import com.quogle.lavarise.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.Registries;

import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    // Step 1: DeferredRegister uses wildcard
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, LavaRise.MOD_ID);

    // Step 2: DeferredHolder uses wildcard as second type
    public static final Supplier<BlockEntityType<DepotBlockEntity>> DEPOT = BLOCK_ENTITY_TYPES.register(
            "depot",
            // The block entity type, created using a builder.
            () -> BlockEntityType.Builder.of(
                            // The supplier to use for constructing the block entity instances.
                            DepotBlockEntity::new,
                            // A vararg of blocks that can have this block entity.
                            // This assumes the existence of the referenced blocks as DeferredBlock<Block>s.
                            ModBlocks.DEPOT_BLOCK.get()
                    )
                    // Build using null; vanilla does some datafixer shenanigans with the parameter that we don't need.
                    .build(null)
    );
}
