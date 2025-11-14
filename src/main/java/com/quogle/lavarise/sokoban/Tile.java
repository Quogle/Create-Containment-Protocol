package com.quogle.lavarise.sokoban;

import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Direction;

import java.util.HashSet;
import java.util.Set;


public class Tile {
    private final int x, y;
    private TileType type;          // WALL, FLOOR, SPIKES, etc.
    private Level level;
    private final Set<Property> properties = new HashSet<>();
    private Entity entity = null;
    private boolean canRotate = false;

    // Only set in tiles that actually use direction
    protected Direction direction;

    public Tile(int x, int y, Level level) {
        this.x = x;
        this.y = y;
        this.type = TileType.VOID;
        this.level = level;
        this.direction = Direction.RIGHT;
    }

    public boolean isWalkable() {
        return type != TileType.WALL && entity == null;
    }

    public boolean hasProperty(Property property) {
        return properties.contains(property);
    }

    public Tile addProperty(Property property) {
        properties.add(property);
        return this;
    }
    public Tile removeProperty(Property property) {
        properties.remove(property);
        return this;
    }

    public Set<Property> getProperties() {
        return properties;
    }
    public Tile clearProperties() {
        // Remove only transferable properties
        properties.removeIf(Property::isTransferable);
        return this;
    }


    public TileType getType() { return type; }
    public Tile setType(TileType type) {
        this.type = type;
        return this;
    }

    public Level getLevel(){
        return level;
    }

    // Default rotation does nothing
    public void rotateClockwise() {
        // no-op for normal tiles
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean hasEntity() { return entity != null; }
    public Entity getEntity() { return entity; }
    public void setEntity(Entity entity) { this.entity = entity; }
    public boolean getCanRotate() { return canRotate; }
    public void setCanRotate(boolean canRotate) { this.canRotate = canRotate; }
}
