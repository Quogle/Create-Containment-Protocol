package com.quogle.lavarise.entity;

import com.quogle.lavarise.LavaRise;
import com.quogle.lavarise.entity.custom.DacoEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, LavaRise.MOD_ID);

    public static final Supplier<EntityType<DacoEntity>> DACO =
            ENTITY_TYPES.register("daco", () -> EntityType.Builder.of(DacoEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 0.75f)
                    .eyeHeight(0.5f)
                    .build(""));



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
