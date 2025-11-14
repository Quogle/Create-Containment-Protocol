package com.quogle.lavarise.client;

import com.quogle.lavarise.client.sokoban.SokobanScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import com.quogle.lavarise.LavaRise; // make sure this points to your main mod class

@EventBusSubscriber(modid = LavaRise.MOD_ID, value = Dist.CLIENT)
public class KeybindHandler {

    // Define a keybind (default: O)
    public static final KeyMapping OPEN_SOKOBAN_KEY = new KeyMapping(
            "key.lavarise.opensokoban",
            GLFW.GLFW_KEY_O,
            "key.categories.lavarise"
    );

    // Register the tick event
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (OPEN_SOKOBAN_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new SokobanScreen());
        }
    }
}
