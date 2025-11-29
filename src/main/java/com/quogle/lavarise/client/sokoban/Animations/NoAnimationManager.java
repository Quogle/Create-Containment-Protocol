package com.quogle.lavarise.client.sokoban.Animations;

import net.minecraft.resources.ResourceLocation;

public class NoAnimationManager extends AnimationManager {

    @Override
    public void tickAll() {
        // do nothing
    }

    @Override
    public Animation get(String name) {
        return new Animation(new ResourceLocation[]{}, new int[]{}, true); // dummy animation
    }

    @Override
    public void add(String name, ResourceLocation[] frames, int[] frameDurations, boolean loop) {
        // do nothing
    }

    @Override
    public void add(String name, ResourceLocation[] frames, int ticksPerFrame, boolean loop) {
        // do nothing
    }
}