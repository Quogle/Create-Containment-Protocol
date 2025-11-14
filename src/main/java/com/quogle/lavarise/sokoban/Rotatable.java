package com.quogle.lavarise.sokoban;

import net.minecraft.resources.ResourceLocation;

public interface Rotatable {
    boolean canRotate();
    void rotateClockwise();
    ResourceLocation getPreview(Direction dir);
    default Direction getDirection() {
        return Direction.DOWN; // default if not overridden
    }
}

