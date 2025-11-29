package com.quogle.lavarise.sokoban.Tiles;

import com.quogle.lavarise.client.sokoban.SokobanScreen;
import com.quogle.lavarise.sokoban.Assets;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tile;
import com.quogle.lavarise.sokoban.TileType;
import net.minecraft.resources.ResourceLocation;

public class ExitTile extends Tile {
    SokobanScreen sokobanScreen;

    public ExitTile(int x, int y, Level level) {
        super(x, y, level);
        this.setType(TileType.EXIT);
    }

    public ResourceLocation getSprite() {
        return Assets.EXIT;
    }
}
