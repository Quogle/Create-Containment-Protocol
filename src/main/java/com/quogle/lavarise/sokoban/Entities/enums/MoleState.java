package com.quogle.lavarise.sokoban.Entities.enums;

import com.quogle.lavarise.sokoban.AnimatableState;

public enum MoleState implements AnimatableState {
    IDLE("idle", null),
    BLOCK("block", IDLE),
    HIDE("hide", IDLE);

    private final String animKey;
    private final MoleState nextState;

    MoleState(String animKey, MoleState nextState) {
        this.animKey = animKey;
        this.nextState = nextState;
    }

    @Override
    public String getAnimKey() { return animKey; }
    @Override
    public MoleState getNextState() { return nextState; }
}