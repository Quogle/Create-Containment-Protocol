package com.quogle.lavarise.sokoban;

public enum Direction {
    NONE(0,0),
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction nextClockwise() {
        return switch (this) {
            case NONE    -> NONE;
            case UP    -> RIGHT;
            case DOWN  -> LEFT;
            case LEFT  -> UP;
            case RIGHT -> DOWN;
        };
    }
}
