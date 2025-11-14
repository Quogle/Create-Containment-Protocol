package com.quogle.lavarise.sokoban.Entities.enums;

public enum SnailState {
    SNAIL_UP("idle_up", null),
    SNAIL_DOWN("idle_down", null),
    SNAIL_LEFT("idle_left", null),
    SNAIL_RIGHT("idle_right", null);

    private final String animKey;
    private final SnailState nextState;

    SnailState(String animKey, SnailState nextState) {
        this.animKey = animKey;
        this.nextState = nextState;
    }

    public String getAnimKey() { return animKey; }
    public SnailState getNextState() { return nextState; }
    public boolean hasNextState() { return nextState != null; }
}
