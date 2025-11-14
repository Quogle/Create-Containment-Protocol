package com.quogle.lavarise.client.sokoban.Animations;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    private final Map<String, Animation> animations = new HashMap<>();

    public void add(String name, ResourceLocation[] frames, int[] frameDurations, boolean loop) {
        animations.put(name, new Animation(frames, frameDurations, loop));
    }

    public void add(String name, ResourceLocation[] frames, int ticksPerFrame, boolean loop) {
        int[] durations = new int[frames.length];
        for (int i = 0; i < frames.length; i++) {
            durations[i] = ticksPerFrame;
        }
        animations.put(name, new Animation(frames, durations, loop));
    }

    public Animation get(String name) {
        return animations.get(name);
    }

    public void tickAll() {
        for (Animation anim : animations.values()) {
            anim.tick();
        }
    }
}
