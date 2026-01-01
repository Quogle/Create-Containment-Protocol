package com.quogle.lavarise.sokoban.Tiles;

import com.quogle.lavarise.client.sokoban.SokobanScreen;
import com.quogle.lavarise.sokoban.Assets;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tile;
import com.quogle.lavarise.sokoban.TileType;
import net.minecraft.resources.ResourceLocation;

public class ExitTile extends Tile {

    private boolean active = false;

    public ExitTile(int x, int y, Level level) {
        super(x, y, level);
        this.setType(TileType.EXIT);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public ResourceLocation getSprite() {
        return active ? Assets.EXIT_ACTIVE : Assets.EXIT_INACTIVE;
    }
}

