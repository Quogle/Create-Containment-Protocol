package com.quogle.lavarise.event;

import com.quogle.lavarise.entity.ModEntities;
import com.quogle.lavarise.entity.custom.DacoEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class ModEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DACO.get(), DacoEntity.createAttributes().build());
    }
}
