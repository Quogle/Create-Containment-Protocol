package com.quogle.lavarise.sokoban.Tiles;

import com.quogle.lavarise.sokoban.Assets;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tile;
import com.quogle.lavarise.sokoban.TileType;
import net.minecraft.resources.ResourceLocation;

public class FloorNumTile extends Tile {
    private int floorNumber = 0;

    public FloorNumTile(int x, int y, Level level) {
        super(x, y, level);
        this.setType(TileType.NUMBER);
    }

    public void setNumber(int newNum) {
        floorNumber = newNum;
    }

    public ResourceLocation getBackground() {
        return Assets.BLANK;
    }

    public ResourceLocation getLeftDigit() {
        int left = floorNumber / 10;
        return Assets.NUMBER_DIGITS[left];
    }

    public ResourceLocation getRightDigit() {
        int right = floorNumber % 10;
        return Assets.NUMBER_DIGITS_RIGHT[right];
    }
}
