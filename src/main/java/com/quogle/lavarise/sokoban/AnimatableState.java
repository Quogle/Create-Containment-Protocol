package com.quogle.lavarise.sokoban;

import com.quogle.lavarise.client.sokoban.Animations.StateMachine;

public interface AnimatableState {
    String getAnimKey();
    AnimatableState getNextState();  // returns next state if any
    default boolean hasNextState() {
        return getNextState() != null;
    }
}


