package com.quogle.lavarise.client;

import com.quogle.lavarise.LavaRise;
import com.quogle.lavarise.menu.DepotScreen;
import com.quogle.lavarise.menu.ModMenuTypes;
import com.quogle.lavarise.entity.ModEntities;
import com.quogle.lavarise.entity.client.DacoRenderer;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.DACO.get(), DacoRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.DEPOT_MENU.get(), DepotScreen::new);
    }
}
