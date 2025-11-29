package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationAssets;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.client.sokoban.Animations.StateMachine;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Tiles.ArrowTile;
import com.quogle.lavarise.sokoban.Tiles.ExitTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static com.quogle.lavarise.sokoban.TileType.VOID;

public class Player extends Entity {

    ExitTile exit;
    public float posX, posY; //render & movement coordinates
    private float speed = 1f;  // units per second

    Tile previousTile;
    private int lastDx = 0;
    private int lastDy = 1;

    public Player(int x, int y, AnimationManager animManager, EntityType entityType, Level level) {
        super(x, y, entityType, animManager);
        this.stateMachine = new StateMachine(PlayerState.IDLE_FRONT);
        this.previousTile = level.getTile(getX(),getY());
        initAnimations();
    }

    private void initAnimations() {
        getAnimationManager().add("idle_front", AnimationAssets.IDLE_FRONT, 40, true);
        getAnimationManager().add("idle_back", AnimationAssets.IDLE_BACK, 40, true);
        getAnimationManager().add("idle_left", AnimationAssets.IDLE_LEFT, 40, true);
        getAnimationManager().add("idle_right", AnimationAssets.IDLE_RIGHT, 40, true);

        getAnimationManager().add("push_front", AnimationAssets.FRONT_PUSH, AnimationAssets.PUSH_DURATIONS, false);
        getAnimationManager().add("push_back", AnimationAssets.BACK_PUSH, AnimationAssets.PUSH_DURATIONS, false);
        getAnimationManager().add("push_left", AnimationAssets.LEFT_PUSH, AnimationAssets.PUSH_DURATIONS, false);
        getAnimationManager().add("push_right", AnimationAssets.RIGHT_PUSH, AnimationAssets.PUSH_DURATIONS, false);
    }

    @Override
    public String getCurrentAnimationKey() {
        return stateMachine.getState().getAnimKey();
    }

    public void move(int dx, int dy, Level level) {
        // Update last movement direction
        if (dx != 0 || dy != 0) {
            lastDx = dx;
            lastDy = dy;
        }

        int newX = getX() + dx;
        int newY = getY() + dy;

        // Check bounds
        if (isOutOfBounds(newX, newY, level)) {
            setIdleOrPushState(dx, dy, true); // blocked, still play push
            moveIceEntities(level, null);
            onPlayerMove(level);
            return;
        }

        Tile targetTile = level.getTile(newX, newY);

        // Walkable tile
        if (canMoveToTile(targetTile)) {
            moveToTile(newX, newY, dx, dy, level, null);
            return;
        }
        // Blocked tile
        setIdleOrPushState(dx, dy, true); // blocked, still play push

        Entity pushedEntity = null;
        // Pushable Entity Logic
        if (targetTile.hasEntity()) {
            pushedEntity = targetTile.getEntity();
            handlePushableEntity(pushedEntity, dx, dy, level);
        }

        moveIceEntities(level, pushedEntity);

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
        level.addHudProperty(this, selectedTile.getType()); //if BASIC is selected update HUD

        for (Property prop : propertiesFromTileType(selectedTile)) {
            if (!prop.isTransferable()) continue; // skip SELECTABLE
            if (targetTile.hasEntity()) {
                targetTile.getEntity().clearProperties();
                targetTile.getEntity().addProperty(prop);
            } else {
                targetTile.clearProperties();
                targetTile.addProperty(prop);
                targetTile.removeProperty(Property.NONE);
            }
            Tile firstBasic = level.getFirstBasicSelectableTile();
            if (selectedTile.getType() != TileType.BASIC) {
                selectedTile.removeProperty(Property.SELECTABLE);
                selectedTile.setType(TileType.BLANK);
                if (firstBasic != null) {
                    level.moveCursorTo(firstBasic);  // you add this method (2 lines below)
                } else {
                    level.cycleCursors();            // fallback to normal cycle behavior
                }
            }
            level.refreshAllCursorTiles();
        }
        onPlayerMove(level);
    }

    public void onPlayerMove(Level level) {
        Tile[][] tiles = level.getTiles();
        int width = level.getWidth();
        int height = level.getHeight();

        Tile playerTile = level.getTile(getX(),getY());
        if(playerTile != previousTile) {
            if(previousTile.getType() == TileType.CRACKED && !previousTile.hasProperty(Property.ICE)) {
                previousTile.setType(VOID);
            }
        }
        previousTile = playerTile;

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
            getAnimationManager().get(newState.getAnimKey()).reset();
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
            getAnimationManager().get(newState.getAnimKey()).reset();
        }

        // Update last movement direction for interactions
        lastDx = (dx > 0 ? 1 : dx < 0 ? -1 : 0);
        lastDy = (dy > 0 ? 1 : dy < 0 ? -1 : 0);
    }

    //Maps selectable cursor tiles to which property they transfer
    private Property[] propertiesFromTileType(Tile tile) {
        return switch (tile.getType()) {
            case BASIC -> new Property[]{Property.NONE};
            case FIRE -> new Property[]{ Property.FIRE };
            case ICE  -> new Property[]{ Property.ICE };
            case WATER -> new Property[]{ Property.WATER };
            case ROTATE -> new Property[]{ Property.ROTATE };
            default -> tile.getProperties().toArray(new Property[0]); // fallback to actual properties
        };
    }

    // --- Helper Functions ---

    private boolean isOutOfBounds(int x, int y, Level level) {
        return x < 0 || x >= level.getWidth() || y < 0 || y >= level.getHeight();
    }

    private boolean canMoveToTile(Tile tile) {
        return !tile.hasEntity() && !tile.getType().isSolid();
    }

    private void moveToTile(int x, int y, int dx, int dy, Level level, Entity pushedEntity) {
        setPosition(x, y);
        setIdleOrPushState(dx, dy, false);
        onPlayerMove(level);
        moveIceEntities(level, pushedEntity);
    }

    private void handlePushableEntity(Entity entity, int dx, int dy, Level level) {
        if (!entity.isPushable()) return;

        Tile currentTile = level.getTile(entity.getX(), entity.getY());

        // --- ROTATE PUSH DIRECTION IF THE ROCK IS STANDING ON A ROTATE TILE ---
        if (currentTile.hasProperty(Property.ROTATE)) {
            int rotatedDx = -dy;
            int rotatedDy = dx;

            dx = rotatedDx;
            dy = rotatedDy;
        }

        // If the rock has ICE property, update its slide direction to the NEW direction
        if (entity.hasProperty(Property.ICE)) {
            entity.setSlideDirection(dx, dy);
        }

        int entityNewX = entity.getX() + dx;
        int entityNewY = entity.getY() + dy;

        // Out of bounds = blocked
        if (isOutOfBounds(entityNewX, entityNewY, level)) {
            setIdleOrPushState(dx, dy, true);
            return;
        }

        Tile entityTargetTile = level.getTile(entityNewX, entityNewY);

        // Can push into target?
        if (canMoveToTile(entityTargetTile)) {

            // Move entity
            currentTile.setEntity(null);
            entityTargetTile.setEntity(entity);
            entity.setPosition(entityNewX, entityNewY);

            // --- BREAK CRACKED TILE WE ARE LEAVING ---
            if (currentTile.getType() == TileType.CRACKED && !currentTile.hasProperty(Property.ICE)) {
                currentTile.setType(VOID);
            }

            // --- ROTATE AGAIN IF THE TARGET TILE HAS ROTATE ---
            if (entityTargetTile.hasProperty(Property.ROTATE)) {
                int rotatedDx = -dy;
                int rotatedDy = dx;
                entity.setSlideDirection(rotatedDx, rotatedDy);
            }
            else if (!entity.hasProperty(Property.ICE)) {
                entity.clearSlide();
            }

        } else {
            setIdleOrPushState(dx, dy, true);
        }
    }


    private void moveIceEntities(Level level, Entity skipEntity) {
        for (Entity entity : level.getEntities()) {
            if (entity == skipEntity) continue;
            if (!(entity.hasProperty(Property.ICE) && (entity.getSlideDx() != 0 || entity.getSlideDy() != 0))) continue;

            int dx = entity.getSlideDx();
            int dy = entity.getSlideDy();

            int newX = entity.getX() + dx;
            int newY = entity.getY() + dy;

            // Check bounds
            if (newX < 0 || newX >= level.getWidth() || newY < 0 || newY >= level.getHeight()) {
                entity.clearSlide();
                continue;
            }

            Tile targetTile = level.getTile(newX, newY);
            Tile currentTile = level.getTile(entity.getX(), entity.getY());

            // Stop sliding if blocked
            if (targetTile.hasEntity() || targetTile.getType().isSolid()) {
                entity.clearSlide();
                continue;
            }

            // --- BREAK CRACKED TILE WE ARE LEAVING ---
            if (currentTile.getType() == TileType.CRACKED && !currentTile.hasProperty(Property.ICE)) {
                currentTile.setType(VOID);
            }

            // Move entity
            currentTile.setEntity(null);
            targetTile.setEntity(entity);
            entity.setPosition(newX, newY);

            // Handle ROTATE property
            if (targetTile.hasProperty(Property.ROTATE)) {
                int rotatedDx = -dy;
                int rotatedDy = dx;
                entity.setSlideDirection(rotatedDx, rotatedDy);
            }
        }
    }



    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }
}
