package com.quogle.lavarise.sokoban.Level;

import com.quogle.lavarise.client.sokoban.Animations.AnimationAssets;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.Box;
import com.quogle.lavarise.sokoban.Entities.Player;
import com.quogle.lavarise.sokoban.Entities.Snail;
import com.quogle.lavarise.sokoban.Tiles.ArrowTile;

public class EditorCursor extends Entity {

    EditorController editorController;
    private final Level level;

    public EditorCursor(Level level, EditorController editorController, EntityType entityType) {
        super(0, 0, entityType);
        this.level = level;
        this.editorController = editorController;
    }

    /** Move cursor by dx, dy (clamped to level bounds) */
    public void move(int dx, int dy, boolean isZHeld) {
        int newX = Math.max(0, Math.min(level.getWidth() - 1, getX() + dx));
        int newY = Math.max(0, Math.min(level.getHeight() - 1, getY() + dy));
        setPosition(newX, newY);

        // Place the currently selected item if X is held
        if (isZHeld) {
            // Use the editor controller’s selected item
            editorController.placeSelectedItem();
        }
    }

    /** Snap to a specific tile */
    public void snapToTile(Tile tile) {
        setPosition(tile.getX(), tile.getY());
    }

    /** Place a tile of the specified type at the cursor */
    public void placeTile(TileType type, Direction dir) {
        int x = getX();
        int y = getY();

        Tile tileToPlace;

        if (type == TileType.ARROW) {
            // Default direction
            tileToPlace = new ArrowTile(x, y, dir, level);
        } else {
            tileToPlace = new Tile(x, y, level);
            tileToPlace.setType(type);
        }

        // Replace the tile in the level
        level.setTile(x, y, tileToPlace);
    }


    /** Apply a property to the current tile */
    public void addPropertyToTile(Property prop) {
        Tile t = level.getTile(getX(), getY());
        if (t != null) t.addProperty(prop);
    }

    /** Remove a property from the current tile */
    public void removePropertyFromTile(Property prop) {
        Tile t = level.getTile(getX(), getY());
        if (t != null) t.removeProperty(prop);
    }

    public void placeEntity(EntityType type, Direction dir) {
        Tile tile = level.getTile(getX(), getY());
        if (tile == null) return;

        switch (type) {
            case PLAYER -> {
                AnimationManager anim = new AnimationManager();
                Player p = new Player(getX(), getY(), anim, getType());
                level.addPlayer(p);   // add player to player list
                tile.setEntity(null); // clear any box or entity on tile
            }
            case BOX -> {
                Box b = new Box(getX(), getY(), getType());
                level.addBox(b);
                tile.setEntity(b);    // normal box placement
            }
            case SNAIL -> {
                AnimationManager anim = new AnimationManager();
                Snail s = new Snail(getX(), getY(), dir, anim, getType());
                level.addSnail(s);
                tile.setEntity(s);
            }
        }
    }

    public Tile getTileUnderCursor() {
        return level.getTile(getX(), getY());
    }
    public Entity getEntityUnderCursor() {
        return level.getEntity(getX(), getY());
    }

    public void removeEntity() {
        Tile t = level.getTile(getX(), getY());
        if (t != null) t.setEntity(null);
    }
    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }
}
