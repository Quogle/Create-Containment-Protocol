package com.quogle.lavarise.sokoban.Level;

import com.quogle.lavarise.sokoban.*;

public class LevelBuilder {

    public static Level createExampleLevel() {
        Level level = new Level();

        for (int x = 0; x < level.getWidth(); x++) {
            level.getTile(x, level.getHeight() - 1).setType(TileType.BLANK);
        }
        level.getTile(1, level.getHeight() - 1).setType(TileType.HP);
        level.getTile(2, level.getHeight() - 1).setType(TileType.NUMBER);
        level.getTile(4, level.getHeight() - 1).setType(TileType.BASIC).addProperty(Property.SELECTABLE);
        level.getTile(5, level.getHeight() - 1).setType(TileType.ROTATE).addProperty(Property.SELECTABLE);
        level.getTile(6, level.getHeight() - 1).setType(TileType.FIRE).addProperty(Property.SELECTABLE);
        level.getTile(7, level.getHeight() - 1).setType(TileType.ICE).addProperty(Property.SELECTABLE);
        level.getTile(8, level.getHeight() - 1).setType(TileType.WATER).addProperty(Property.SELECTABLE);


        level.getTile(12, level.getHeight() - 1).setType(TileType.FL);
        level.getTile(13, level.getHeight() - 1).setType(TileType.NUMBER);

        return level;
    }
}
