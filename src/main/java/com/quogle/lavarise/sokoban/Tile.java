package com.quogle.lavarise.sokoban;

import com.quogle.lavarise.sokoban.Entities.Entity;
import com.quogle.lavarise.sokoban.Entities.Mole;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import com.quogle.lavarise.sokoban.Level.Level;

import java.util.HashSet;
import java.util.Set;


public class Tile {
    private final int x, y;
    private TileType type;          // WALL, FLOOR, SPIKES, etc.
    private Level level;
    private final Set<Anomaly> anomalies = new HashSet<>();
    private Entity entity = null;
    private boolean canRotate = false;
    private Mole mole;
    private TileState state = TileState.NONE; // <-- new


    // Only set in tiles that actually use direction
    protected Direction direction;

    public Tile(int x, int y, Level level) {
        this.x = x;
        this.y = y;
        this.type = TileType.VOID;
        this.level = level;
        this.direction = Direction.RIGHT;
    }

    public boolean isWalkable(Entity entityTryingToEnter) {
        // Tile itself not solid
        if (type == TileType.WALL) return false;

        // Normal entity blocks unless stackable
        if (this.entity != null && !this.entity.isStackable()) return false;

        // Mole blocks only players
        return mole == null || entityTryingToEnter.getType() != EntityType.PLAYER;
    }



    public boolean hasProperty(Anomaly property) {
        return anomalies.contains(property);
    }

    public Tile addProperty(Anomaly property) {
        anomalies.add(property);
        return this;
    }
    public Tile setProperties(Anomaly property) {
        anomalies.add(property);
        return this;
    }

    public Tile removeProperty(Anomaly property) {
        anomalies.remove(property);
        return this;
    }

    public Set<Anomaly> getProperties() {
        return anomalies;
    }
    public Tile clearProperties() {
        // Remove only transferable properties
        anomalies.removeIf(Anomaly::isTransferable);
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
    public void setEntity(Entity newEntity) {
        if (newEntity instanceof Mole) {
            this.mole = (Mole) newEntity; // store in mole field only
            return;
        }
        this.entity = newEntity; // all other normal entities
    }

    public TileState getState() { return state; }
    public void setState(TileState newState) { this.state = newState; }
    public boolean isLethal() { return state == TileState.LETHAL; }


    public boolean hasNonMoleEntity() {
        return entity != null && !(entity instanceof Mole);
    }

    public void setMole(Mole m) {
        this.mole = m;
    }

    public boolean hasMole() {
        return mole != null;
    }

    public Mole getMole() {
        return mole;
    }


    public boolean getCanRotate() { return canRotate; }
    public void setCanRotate(boolean canRotate) { this.canRotate = canRotate; }
}
