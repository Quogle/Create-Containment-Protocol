package com.quogle.lavarise.client.sokoban.Animations;

import com.quogle.lavarise.sokoban.AnimatableState;
import java.util.HashMap;
import java.util.Map;

public class StateMachine {
    private AnimatableState currentState;
    private final Map<AnimatableState, Runnable> onEnterActions = new HashMap<>();
    private final Map<AnimatableState, Runnable> onExitActions = new HashMap<>();
    private final Map<AnimatableState, Runnable> onTickActions = new HashMap<>();

    public StateMachine(AnimatableState initialState) {
        this.currentState = initialState;
    }

    public AnimatableState getState() {
        return currentState;
    }

    public void setState(AnimatableState newState) {
        if (newState == currentState) return;

        if (onExitActions.containsKey(currentState))
            onExitActions.get(currentState).run();

        currentState = newState;

        if (onEnterActions.containsKey(newState))
            onEnterActions.get(newState).run();
    }

    public void tick() {
        Runnable tickAction = onTickActions.get(currentState);
        if (tickAction != null) tickAction.run();

        // Automatically transition to next state if it exists
        if (currentState.hasNextState()) {
            setState(currentState.getNextState());
        }
    }

    public void onTick(AnimatableState state, Runnable action) { onTickActions.put(state, action); }
    public void onEnter(AnimatableState state, Runnable action) { onEnterActions.put(state, action); }
    public void onExit(AnimatableState state, Runnable action) { onExitActions.put(state, action); }
}
