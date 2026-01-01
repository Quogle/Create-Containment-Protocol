package com.quogle.lavarise.sokoban.Tiles;

import com.quogle.lavarise.sokoban.Assets;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tile;
import com.quogle.lavarise.sokoban.TileType;
import net.minecraft.resources.ResourceLocation;

public class ButtonTile extends Tile {
    boolean pressed;
    public ButtonTile(int x, int y, Level level) {
        super(x, y, level);
        this.setType(TileType.BUTTON);
    }

    public boolean isPressed() { return pressed; }
    public void setPressed(boolean isPressed) {
        pressed = isPressed;
    }

    public ResourceLocation getSprite() {
        return Assets.BUTTON;
    }
}
