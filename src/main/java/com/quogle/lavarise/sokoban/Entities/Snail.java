package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationAssets;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.client.sokoban.Animations.StateMachine;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.enums.SnailState;
import com.quogle.lavarise.sokoban.Level.Level;
import net.minecraft.client.gui.GuiGraphics;
import com.quogle.lavarise.sokoban.Direction;
import net.minecraft.resources.ResourceLocation;

import static com.quogle.lavarise.sokoban.TileType.VOID;

public class Snail extends Entity implements Rotatable {
    private Direction direction;
    private Property property;
    Tile previousTile;
    //Constructor
    public Snail(int x, int y, Direction dir, AnimationManager animManager, EntityType entityType, Level level) {
        super(x, y, entityType, animManager);
        this.stateMachine = new StateMachine(SnailState.SNAIL_LEFT);
        this.direction = dir;
        this.previousTile = level.getTile(getX(),getY());
        initAnimations();
        updateVisualDirection();
    }

    //Make snail move a step, called when player moves
    public void moveOneStep(Level level) {
        Tile currentTile = level.getTile(getX(), getY());

        // Check if frozen (on ice)
        if (this.isFrozen()) {
            applyTileEffects(currentTile);
            return; // skip movement entirely
        }

        //get new tile based on direction player is facing
        int newX = getX() + direction.dx;
        int newY = getY() + direction.dy;

        // Check level bounds
        if (newX < 0 || newX >= level.getWidth() || newY < 0 || newY >= level.getHeight()) {
            turnAround();
            return;
        }

        //set target tile coords
        Tile targetTile = level.getTile(newX, newY);

        // Check if tile is walkable and no solid entity
        if (!targetTile.getType().isSolid() && (!targetTile.hasEntity() || !targetTile.getEntity().getType().isSolid())) {
            // Move snail
            level.getTile(getX(), getY()).setEntity(null); // clear current tile
            setPosition(newX, newY);
            targetTile.setEntity(this);

            Tile newCurrentTile = level.getTile(getX(), getY());
            if(newCurrentTile != previousTile && !previousTile.hasProperty(Property.ICE)) {
                if(previousTile.getType() == TileType.CRACKED) {
                    previousTile.setType(VOID);
                }
            }
            previousTile = newCurrentTile;

        } else {
            turnAround();
        }
        applyTileEffects(targetTile);
    }

    //Turn snail 180 degrees, called when it's movement gets blocked.
    private void turnAround() {
        switch (direction) {
            case UP -> direction = Direction.DOWN;
            case DOWN -> direction = Direction.UP;
            case LEFT -> direction = Direction.RIGHT;
            case RIGHT -> direction = Direction.LEFT;
        }
        updateVisualDirection();
    }

    //apply Tile properties to snail
    private void applyTileEffects(Tile tile) {
        if (tile.hasProperty(Property.ROTATE)) {
            this.direction = this.direction.nextClockwise();
            updateVisualDirection();
        }
        if (tile.hasProperty(Property.ICE)) {
            this.setFrozen(true);
            this.setPushableOverride(true);
        }
        else {
            this.setFrozen(false);
            this.setPushableOverride(false);
        }
    }

    public boolean canRotate() {
        return true;
    }


    @Override
    public void rotateClockwise() {
        direction = switch (direction) {
            case UP -> Direction.RIGHT;
            case RIGHT -> Direction.DOWN;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
        };
        updateVisualDirection();
    }

    //Preview for Editor
    @Override
    public ResourceLocation getPreview(Direction dir) {
        return switch(dir) {
            case UP -> Assets.SNAIL_UP;
            case DOWN -> Assets.SNAIL_DOWN;
            case LEFT -> Assets.SNAIL_LEFT;
            case RIGHT -> Assets.SNAIL_RIGHT;
        };
    }


    private void initAnimations() {
        getAnimationManager().add("idle_left", AnimationAssets.SNAIL_LEFT, 40, true);
        getAnimationManager().add("idle_right", AnimationAssets.SNAIL_RIGHT, 40, true);
        getAnimationManager().add("idle_up", AnimationAssets.SNAIL_UP, 40, true);
        getAnimationManager().add("idle_down", AnimationAssets.SNAIL_DOWN, 40, true);
    }

    public void render(GuiGraphics guiGraphics, int offsetX, int offsetY, int tileSize) {
        // Tick the snail's animation
        getAnimationManager().tickAll();
        Animation anim = getAnimationManager().get(getCurrentState().getAnimKey());
        super.render(guiGraphics, offsetX, offsetY, tileSize, anim, getX(), getY());
    }

    private void updateVisualDirection() {
        switch (direction) {
            case UP -> stateMachine.setState(SnailState.SNAIL_UP);
            case DOWN -> stateMachine.setState(SnailState.SNAIL_DOWN);
            case LEFT -> stateMachine.setState(SnailState.SNAIL_LEFT);
            case RIGHT -> stateMachine.setState(SnailState.SNAIL_RIGHT);
        }
        // Reset animation so it starts from first frame
        getAnimationManager().get(stateMachine.getState().getAnimKey()).reset();
    }

    @Override
    public String getCurrentAnimationKey() {
        return stateMachine.getState().getAnimKey();
    }

    @Override
    public Direction getDirection() { return direction; }

    //Animation Getters
    public SnailState getCurrentState() {
        return (SnailState) stateMachine.getState();
    }
    public StateMachine getStateMachine() {
        return stateMachine;
    }
    @Override
    public EntityType getType() {
        return EntityType.SNAIL;
    }
    public Tile getPreviousTile() {return previousTile;}
    public void setPreviousTile(Tile newTile) {this.previousTile = newTile;}
}
