package com.quogle.lavarise.client.sokoban.Animations;

public class BeatSync {
    private final float beatsPerTick;
    private float beatAccumulator = 0f;

    public BeatSync(int bpm) {
        this.beatsPerTick = (bpm / 60f) / 20f;
    }

    /** Call every tick. Returns true if a beat occurs this tick. */
    public boolean tickBeat() {
        beatAccumulator += beatsPerTick;
        if (beatAccumulator >= 1f) {
            beatAccumulator -= 1f;
            return true;
        }
        return false;
    }

    /** Optional: reset the counter */
    public void reset() {
        beatAccumulator = 0f;
    }
}
