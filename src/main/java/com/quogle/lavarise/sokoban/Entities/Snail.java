package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationAssets;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.client.sokoban.Animations.StateMachine;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import com.quogle.lavarise.sokoban.Entities.enums.SnailState;
import com.quogle.lavarise.sokoban.Level.Level;
import net.minecraft.client.gui.GuiGraphics;
import com.quogle.lavarise.sokoban.Direction;
import net.minecraft.resources.ResourceLocation;

import static com.quogle.lavarise.sokoban.TileType.VOID;

public class Snail extends Entity implements Rotatable {
    private boolean canCollide = false;
    private Anomaly property;
    Tile previousTile;
    //Constructor
    public Snail(int x, int y, Direction dir, AnimationManager animManager, EntityType entityType, Level level) {
        super(x, y, entityType, animManager);
        this.stateMachine = new StateMachine(SnailState.SNAIL_LEFT);
        setDirection(dir);
        this.previousTile = level.getTile(getX(),getY());
        initAnimations();
        updateVisualDirection();
    }

    //Make snail move a step, called when player moves
    public void moveOneStep(Level level) {
        Tile currentTile = level.getTile(getX(), getY());

        System.out.println("SNAIL MOVCE!!!!!");

        // Check if frozen (on ice)
        if (this.isFrozen()) {
            applyTileEffects(currentTile);
            return; // skip movement entirely
        }

        //get new tile based on direction they is facing
        int newX = getX() + getDirection().dx;
        int newY = getY() + getDirection().dy;

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
            if (currentTile.getEntity() == this) {
                currentTile.setEntity(null);
            }
            setPosition(newX, newY);
            targetTile.setEntity(this);

            Tile newCurrentTile = level.getTile(getX(), getY());
            if(newCurrentTile != previousTile && !previousTile.hasProperty(Anomaly.ICE)) {
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
        setDirection(switch (getDirection()) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
            default -> Direction.NONE;
        });
        updateVisualDirection();
    }

    //apply Tile properties to snail
    private void applyTileEffects(Tile tile) {
        if (tile.hasProperty(Anomaly.ROTATE)) {
            this.setDirection(this.getDirection().nextClockwise());
            updateVisualDirection();
        }
        if (tile.hasProperty(Anomaly.ICE)) {
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
        setDirection(getDirection().nextClockwise());
        updateVisualDirection();
    }

    //Preview for Editor
    @Override
    public ResourceLocation getPreview(Direction dir) {
        Direction actualDir = dir != null ? dir : getDirection();
        return switch (actualDir) {
            case UP -> Assets.SNAIL_UP;
            case DOWN -> Assets.SNAIL_DOWN;
            case LEFT -> Assets.SNAIL_LEFT;
            case RIGHT -> Assets.SNAIL_RIGHT;
            default -> Assets.SNAIL_UP; // fallback for NONE
        };
    }



    private void initAnimations() {
        getAnimationManager().addIdle("idle_left", AnimationAssets.SNAIL_LEFT, true);
        getAnimationManager().addIdle("idle_right", AnimationAssets.SNAIL_RIGHT, true);
        getAnimationManager().addIdle("idle_up", AnimationAssets.SNAIL_UP, true);
        getAnimationManager().addIdle("idle_down", AnimationAssets.SNAIL_DOWN, true);
    }

    public void render(GuiGraphics guiGraphics, int offsetX, int offsetY, int tileSize) {
        // Tick the snail's animation
        getAnimationManager().tickAll();
        Animation anim = getAnimationManager().get(getCurrentState().getAnimKey());
        super.render(guiGraphics, offsetX, offsetY, tileSize, anim, getX(), getY());
    }

    private void updateVisualDirection() {
        switch (getDirection()) {
            case UP -> stateMachine.setState(SnailState.SNAIL_UP);
            case DOWN -> stateMachine.setState(SnailState.SNAIL_DOWN);
            case LEFT -> stateMachine.setState(SnailState.SNAIL_LEFT);
            case RIGHT -> stateMachine.setState(SnailState.SNAIL_RIGHT);
            default -> {} // NONE does nothing
        }
        getAnimationManager().get(stateMachine.getState().getAnimKey()).reset();
    }

    @Override
    public String getCurrentAnimationKey() {
        return stateMachine.getState().getAnimKey();
    }

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
