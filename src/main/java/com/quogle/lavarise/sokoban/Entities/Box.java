package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.sokoban.Assets;
import com.quogle.lavarise.sokoban.Entities.enums.SnailState;
import com.quogle.lavarise.sokoban.Entity;
import com.quogle.lavarise.sokoban.EntityType;
import net.minecraft.client.gui.GuiGraphics;

public class Box extends Entity {

    public Box(int x, int y, EntityType entityType) {
        super(x, y, entityType);
    }

    @Override
    public EntityType getType() {
        return EntityType.BOX;
    }

    public void render(GuiGraphics guiGraphics, int offsetX, int offsetY, int tileSize) {
        // Draw the snail
        guiGraphics.blit(Assets.BOX,
                offsetX + getX() * tileSize,
                offsetY + getY() * tileSize,
                0, 0, tileSize, tileSize,
                tileSize, tileSize);
    }

    @Override
    public boolean isPushable() {
        return true;
    }
}
