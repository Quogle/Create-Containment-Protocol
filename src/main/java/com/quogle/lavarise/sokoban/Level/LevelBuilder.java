package com.quogle.lavarise.sokoban.Level;

import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Tiles.FloorNumTile;

public class LevelBuilder {

    public static Level createExampleLevel() {
        Level level = new Level();

        for (int x = 0; x < level.getWidth(); x++) {
            level.getTile(x, level.getHeight() - 1).setType(TileType.BLANK);
        }
        level.getTile(1, level.getHeight() - 1).setType(TileType.HP);
        level.setTile(2, level.getHeight() - 1, new FloorNumTile(2, level.getHeight() - 1, level));
        level.getTile(4, level.getHeight() - 1).setType(TileType.BASIC).addProperty(Anomaly.SELECTABLE);
        level.getTile(5, level.getHeight() - 1).setType(TileType.ROTATE).addProperty(Anomaly.SELECTABLE);
        level.getTile(6, level.getHeight() - 1).setType(TileType.FIRE).addProperty(Anomaly.SELECTABLE);
        level.getTile(7, level.getHeight() - 1).setType(TileType.ICE).addProperty(Anomaly.SELECTABLE);
        level.getTile(8, level.getHeight() - 1).setType(TileType.WATER).addProperty(Anomaly.SELECTABLE);


        level.getTile(12, level.getHeight() - 1).setType(TileType.FL);
        level.setTile(13, level.getHeight() - 1, new FloorNumTile(13, level.getHeight() - 1, level));

        return level;
    }
}
