package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.Assets;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tile;
import net.minecraft.resources.ResourceLocation;

public class Box extends Entity {


    Tile previousTile;
    public Box(int x, int y, EntityType entityType, Level level) {
        super(x, y, entityType, createAnimationManager(level));
        this.previousTile = level.getTile(getX(), getY());
    }

    @Override
    public EntityType getType() {
        return EntityType.BOX;
    }

    private static AnimationManager createAnimationManager(Level level) {
        AnimationManager manager = new AnimationManager(level);
        manager.addIdle("DEFAULT", new ResourceLocation[]{Assets.BOX},false);
        return manager;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    public Tile getPreviousTile() {return previousTile;}
    public void setPreviousTile(Tile newTile) {this.previousTile = newTile;}
}
