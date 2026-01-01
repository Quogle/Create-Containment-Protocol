package com.quogle.lavarise.client.sokoban.Animations;

import com.quogle.lavarise.sokoban.Level.Level;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {
    private final Map<String, Animation> animations = new HashMap<>();
    private final Level level;

    public AnimationManager(Level level) {
        this.level = level;
    }

    // Idle animation (BPM-synced)
    public void addIdle(String name, ResourceLocation[] frames, boolean loop) {
        Animation anim = new Animation(frames, loop);
        animations.put(name, anim);
        syncAnimationToLevelBpm(anim);
    }

    // Push animation (custom durations)
    public void add(String name, ResourceLocation[] frames, int[] durations, boolean loop) {
        animations.put(name, new Animation(frames, durations, loop));
    }

    public Animation get(String name) { return animations.get(name); }

    public void tickAll() {
        for (Animation anim : animations.values()) anim.tick();
    }

    public void syncAllIdleAnimations() {
        for (Animation anim : animations.values()) {
            if (anim.frameDurations == null) syncAnimationToLevelBpm(anim);
        }
    }

    private void syncAnimationToLevelBpm(Animation anim) {
        double ticksPerBeat = (60.0 / level.getBpm()) * 20.0 * 2;
        anim.setTicksPerFrame(ticksPerBeat / anim.getFrameCount());
    }

    public void add(String name, ResourceLocation[] frames, int ticksPerFrame, boolean loop) {

    }
}
