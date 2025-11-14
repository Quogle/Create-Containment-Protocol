package com.quogle.lavarise.client.sokoban.Animations;

import net.minecraft.resources.ResourceLocation;

public class Animation {
    private final ResourceLocation[] frames;
    private final int[] frameDurations;

    private int currentFrame = 0;
    private int tickCounter = 0;
    private boolean finished = false;
    private final boolean loop;

    public Animation(ResourceLocation[] frames, int[] frameDurations, boolean loop) {
        if (frames.length != frameDurations.length) {
            throw new IllegalArgumentException("frames and frameDurations must match in length");
        }
        this.frames = frames;
        this.frameDurations = frameDurations;
        this.loop = loop;
    }

    /** Call every tick to update frame */
    public void tick() {
        if (finished) return;

        tickCounter++;

        // Check if it's time to advance frame
        if (tickCounter >= frameDurations[currentFrame]) {
            tickCounter = 0;
            currentFrame++;

            // End reached
            if (currentFrame >= frames.length) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                    finished = true;
                }
            }
        }
    }

    public ResourceLocation getCurrentFrame() {
        return frames[currentFrame];
    }

    public boolean isFinished() {
        return finished;
    }

    public void reset() {
        currentFrame = 0;
        tickCounter = 0;
        finished = false;
    }
}
