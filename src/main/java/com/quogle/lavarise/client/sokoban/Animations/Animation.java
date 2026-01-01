package com.quogle.lavarise.client.sokoban.Animations;

import net.minecraft.resources.ResourceLocation;

public class Animation {
    private final ResourceLocation[] frames;
    final int[] frameDurations; // optional, null for BPM-synced
    private final boolean loop;

    private int currentFrame = 0;
    private double frameProgress = 0.0; // fractional ticks
    private double ticksPerFrame = 1.0; // only for BPM-synced
    private boolean finished = false;

    // BPM-synced constructor (idle)
    public Animation(ResourceLocation[] frames, boolean loop) {
        this.frames = frames;
        this.loop = loop;
        this.frameDurations = null;
    }

    // Custom per-frame durations (push)
    public Animation(ResourceLocation[] frames, int[] frameDurations, boolean loop) {
        if (frames.length != frameDurations.length) throw new IllegalArgumentException("frames and frameDurations must match");
        this.frames = frames;
        this.frameDurations = frameDurations;
        this.loop = loop;
    }

    public void tick() {
        if (finished) return;

        if (frameDurations != null) {
            // per-frame durations (push)
            frameProgress++;
            while (frameProgress >= frameDurations[currentFrame]) {
                frameProgress -= frameDurations[currentFrame];
                currentFrame++;
                if (currentFrame >= frames.length) {
                    if (loop) currentFrame = 0;
                    else { currentFrame = frames.length - 1; finished = true; }
                }
            }
        } else {
            // BPM-synced idle
            frameProgress += 1.0;
            while (frameProgress >= ticksPerFrame) {
                frameProgress -= ticksPerFrame;
                currentFrame++;
                if (currentFrame >= frames.length) {
                    if (loop) currentFrame = 0;
                    else { currentFrame = frames.length - 1; finished = true; }
                }
            }
        }
    }

    public void setTicksPerFrame(double ticksPerFrame) { this.ticksPerFrame = ticksPerFrame; }
    public boolean isFinished() { return finished; }
    public void reset() { currentFrame = 0; frameProgress = 0.0; finished = false; }
    public ResourceLocation getCurrentFrame() { return frames[currentFrame]; }
    public int getFrameCount() { return frames.length; }
}
