package com.quogle.lavarise.client.sokoban.Animations;

import java.util.HashMap;
import java.util.Map;

public class StateMachine<T extends Enum<T>> {
    private T currentState;
    private final Map<T, Runnable> onEnterActions = new HashMap<>();
    private final Map<T, Runnable> onExitActions = new HashMap<>();

    public StateMachine(T initialState) {
        this.currentState = initialState;
    }

    public T getState() {
        return currentState;
    }

    public void setState(T newState) {
        if (newState == currentState) return;
        // Exit current
        if (onExitActions.containsKey(currentState)) onExitActions.get(currentState).run();
        // Enter new
        T oldState = currentState;
        currentState = newState;
        if (onEnterActions.containsKey(newState)) onEnterActions.get(newState).run();
    }

    public void tick() {
        Runnable tickAction = onTickActions.get(currentState);
        if (tickAction != null) tickAction.run();
    }

    private final Map<T, Runnable> onTickActions = new HashMap<>();

    public void onTick(T state, Runnable action) {
        onTickActions.put(state, action);
    }

    public void onEnter(T state, Runnable action) {
        onEnterActions.put(state, action);
    }

    public void onExit(T state, Runnable action) {
        onExitActions.put(state, action);
    }
}
