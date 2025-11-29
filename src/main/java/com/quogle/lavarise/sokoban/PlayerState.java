package com.quogle.lavarise.sokoban;

public enum PlayerState implements AnimatableState {
    IDLE_FRONT("idle_front", null),
    IDLE_BACK("idle_back", null),
    IDLE_LEFT("idle_left", null),
    IDLE_RIGHT("idle_right", null),
    PUSH_FRONT("push_front", IDLE_FRONT),
    PUSH_BACK("push_back", IDLE_BACK),
    PUSH_LEFT("push_left", IDLE_LEFT),
    PUSH_RIGHT("push_right", IDLE_RIGHT);

    private final String animKey;
    private final PlayerState nextState;

    PlayerState(String animKey, PlayerState nextState) {
        this.animKey = animKey;
        this.nextState = nextState;
    }

    @Override
    public String getAnimKey() { return animKey; }
    @Override
    public PlayerState getNextState() { return nextState; }
}