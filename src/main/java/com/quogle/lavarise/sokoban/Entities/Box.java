package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.Assets;
import com.quogle.lavarise.sokoban.Entity;
import com.quogle.lavarise.sokoban.EntityType;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tile;
import net.minecraft.resources.ResourceLocation;

public class Box extends Entity {


    Tile previousTile;
    public Box(int x, int y, EntityType entityType, Level level) {
        super(x, y, entityType, createAnimationManager());
        this.previousTile = level.getTile(getX(), getY());
    }

    @Override
    public EntityType getType() {
        return EntityType.BOX;
    }

    private static AnimationManager createAnimationManager() {
        AnimationManager manager = new AnimationManager();
        manager.add("DEFAULT", new ResourceLocation[]{Assets.BOX}, 1, false);
        return manager;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    public Tile getPreviousTile() {return previousTile;}
    public void setPreviousTile(Tile newTile) {this.previousTile = newTile;}
}
