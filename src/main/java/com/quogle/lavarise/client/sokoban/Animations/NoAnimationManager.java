package com.quogle.lavarise.client.sokoban.Animations;

import com.quogle.lavarise.sokoban.Level.Level;
import net.minecraft.resources.ResourceLocation;

public class NoAnimationManager extends AnimationManager {

    public NoAnimationManager(Level level) {
        super(level);
    }

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