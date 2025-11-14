package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationAssets;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.client.sokoban.Animations.StateMachine;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tiles.ArrowTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Player extends Entity {
    private final StateMachine<PlayerState> stateMachine;
    private final AnimationManager animManager;
    public float posX, posY; //render & movement coordinates
    private float speed = 1f;  // units per second


    private int lastDx = 0;
    private int lastDy = 1;

    public Player(int x, int y, AnimationManager animManager, EntityType entityType) {
        super(x, y, entityType);
        this.animManager = animManager;
        this.stateMachine = new StateMachine<>(PlayerState.IDLE_FRONT);
        initAnimations();
    }

    private void initAnimations() {
        animManager.add("idle_front", AnimationAssets.IDLE_FRONT, 40, true);
        animManager.add("idle_back", AnimationAssets.IDLE_BACK, 40, true);
        animManager.add("idle_left", AnimationAssets.IDLE_LEFT, 40, true);
        animManager.add("idle_right", AnimationAssets.IDLE_RIGHT, 40, true);

        animManager.add("push_front", AnimationAssets.FRONT_PUSH, AnimationAssets.PUSH_DURATIONS, false);
        animManager.add("push_back", AnimationAssets.BACK_PUSH, AnimationAssets.PUSH_DURATIONS, false);
        animManager.add("push_left", AnimationAssets.LEFT_PUSH, AnimationAssets.PUSH_DURATIONS, false);
        animManager.add("push_right", AnimationAssets.RIGHT_PUSH, AnimationAssets.PUSH_DURATIONS, false);
    }

    public void move(int dx, int dy, Level level) {
        if (dx != 0 || dy != 0) {
            lastDx = dx;
            lastDy = dy;
        }

        int newX = getX() + dx;
        int newY = getY() + dy;

        // Check bounds
        boolean outOfBounds = newX < 0 || newX >= level.getWidth() || newY < 0 || newY >= level.getHeight();
        if (outOfBounds) {
            setIdleOrPushState(dx, dy, true); // blocked, still play push
            onPlayerMove(level);
            return;
        }

        Tile targetTile = level.getTile(newX, newY);

        // Walkable tile
        if (!targetTile.hasEntity() && !targetTile.getType().isSolid()) {
            setPosition(newX, newY);
            setIdleOrPushState(dx, dy, false);
            onPlayerMove(level);
            return;
        } else {
            setIdleOrPushState(dx, dy, true); // blocked, still play push
        }



        if (targetTile.hasEntity()) {
            Entity entity = targetTile.getEntity();

            if (entity.isPushable()) {
                int entityNewX = newX + dx;
                int entityNewY = newY + dy;

                outOfBounds = entityNewX < 0 || entityNewX >= level.getWidth() || entityNewY < 0 || entityNewY >= level.getHeight();
                Tile entityTargetTile = outOfBounds ? null : level.getTile(entityNewX, entityNewY);

                if (!outOfBounds && !entityTargetTile.hasEntity() && !entityTargetTile.getType().isSolid()) {
                    // Move pushable entity
                    targetTile.setEntity(null);
                    entityTargetTile.setEntity(entity);
                    entity.setPosition(entityNewX, entityNewY);
                }

                setIdleOrPushState(dx, dy, true); // play push animation
            } else {
                // Unpushable entity blocks movement
                setIdleOrPushState(dx, dy, true); // play push animation (optional)
                return; // cancel player movement
            }

        }
        onPlayerMove(level);
    }

    public void moveFree(float dx, float dy, Level level, float deltaTime) {
        float nextX = posX + dx * speed * deltaTime;
        float nextY = posY + dy * speed * deltaTime;

        //Prevent player from entering tiles set as solid
        int tileX = (int) nextX;
        int tileY = (int) nextY;

        if (tileX >= 0 && tileX < level.getWidth() && tileY >= 0 && tileY < level.getHeight()) {
            Tile tile = level.getTile(tileX, tileY);
            if (!tile.getType().isSolid()) {
                posX = nextX;
                posY = nextY;
            }
        }

        // Update animation based on dx/dy
        updateAnimation(dx, dy);
    }

    public Tile getTileInFront(Level level) {
        int tx = getX() + lastDx;
        int ty = getY() + lastDy;

        if (tx < 0 || tx >= level.getWidth() || ty < 0 || ty >= level.getHeight()) {
            return null; // out of bounds
        }

        return level.getTile(tx, ty);
    }

    public void placePropertiesInFront(Level level, Tile selectedTile) {
        if (selectedTile == null) return;

        Tile targetTile = getTileInFront(level);
        if (targetTile == null) return; // out of bounds

        for (Property prop : propertiesFromTileType(selectedTile)) {
            if (!prop.isTransferable()) continue; // skip SELECTABLE
            if (targetTile.hasEntity()) {
                targetTile.getEntity().addProperty(prop);
            } else {
                targetTile.clearProperties();
                targetTile.addProperty(prop);
            }
        }
    }

    public void onPlayerMove(Level level) {
        Tile[][] tiles = level.getTiles();
        int width = level.getWidth();
        int height = level.getHeight();

        //Toggle arrow tile frames
        for (Tile[] row : tiles) {
            for (Tile t : row) {
                if (t instanceof ArrowTile arrow) {
                    arrow.toggleFrame();
                }
            }
        }

        //stage all property transfers
        Map<Tile, List<Property>> staged = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = tiles[y][x];
                if (t instanceof ArrowTile arrow) {

                    // Collect transferable properties
                    List<Property> props = arrow.collectTransferableProperties();
                    if (props.isEmpty()) continue;

                    //get target tile
                    int tx = arrow.getX() + arrow.getDirection().dx;
                    int ty = arrow.getY() + arrow.getDirection().dy;
                    if (tx < 0 || tx >= width || ty < 0 || ty >= height) continue;

                    Tile target = tiles[ty][tx];
                    if (target == null) continue;

                    // Stage properties for target
                    staged.computeIfAbsent(target, k -> new ArrayList<>()).addAll(props);

                    // Remove transferable properties from sender arrow immediately
                    arrow.getProperties().removeIf(Property::isTransferable);
                }
            }
        }

        //apply all staged properties
        for (Map.Entry<Tile, List<Property>> entry : staged.entrySet()) {
            Tile target = entry.getKey();
            List<Property> properties = entry.getValue();

            // Replace target's properties with only incoming ones
            target.clearProperties();
            for (Property p : properties) {
                target.addProperty(p);
            }
        }
        for (Snail s : level.getSnails()) {
            s.moveOneStep(level);
        }
    }




    private void setIdleOrPushState(int dx, int dy, boolean pushing) {
        PlayerState newState;

        if (pushing) {
            newState = dx == 1 ? PlayerState.PUSH_RIGHT :
                    dx == -1 ? PlayerState.PUSH_LEFT :
                            dy == 1 ? PlayerState.PUSH_FRONT :
                                    PlayerState.PUSH_BACK;
        } else {
            newState = dx == -1 ? PlayerState.IDLE_LEFT :
                    dx == 1 ? PlayerState.IDLE_RIGHT :
                            dy == -1 ? PlayerState.IDLE_BACK :
                                    PlayerState.IDLE_FRONT;
        }

        // Only switch if state changed
        if (stateMachine.getState() != newState) {
            stateMachine.setState(newState);

            // Reset the animation so push/idle plays from frame 0
            animManager.get(newState.getAnimKey()).reset();
        }
    }

    public void updateAnimation(float dx, float dy) {
        PlayerState newState;

        if (dx > 0) newState = PlayerState.IDLE_RIGHT;
        else if (dx < 0) newState = PlayerState.IDLE_LEFT;
        else if (dy > 0) newState = PlayerState.IDLE_FRONT;
        else if (dy < 0) newState = PlayerState.IDLE_BACK;
        else {
            // No movement, keep current animation
            return;
        }

        // Only update if state changed
        if (stateMachine.getState() != newState) {
            stateMachine.setState(newState);
            animManager.get(newState.getAnimKey()).reset();
        }

        // Update last movement direction for interactions
        lastDx = (dx > 0 ? 1 : dx < 0 ? -1 : 0);
        lastDy = (dy > 0 ? 1 : dy < 0 ? -1 : 0);
    }

    public void tickAnimations() {
        animManager.tickAll();

        PlayerState state = stateMachine.getState();
        Animation anim = animManager.get(state.getAnimKey());

        if (state.hasNextState() && anim.isFinished()) {
            PlayerState next = state.getNextState();
            stateMachine.setState(next);
            animManager.get(next.getAnimKey()).reset();
        }
    }



    public PlayerState getCurrentState() {
        return stateMachine.getState();
    }

    public AnimationManager getAnimationManager() {
        return animManager;
    }
    public StateMachine<PlayerState> getStateMachine() {
        return stateMachine;
    }

    //Maps selectable cursor tiles to which property they transfer
    private Property[] propertiesFromTileType(Tile tile) {
        return switch (tile.getType()) {
            case FIRE -> new Property[]{ Property.FIRE };
            case ICE  -> new Property[]{ Property.ICE };
            case WATER -> new Property[]{ Property.WATER };
            default -> tile.getProperties().toArray(new Property[0]); // fallback to actual properties
        };
    }
    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }
}
