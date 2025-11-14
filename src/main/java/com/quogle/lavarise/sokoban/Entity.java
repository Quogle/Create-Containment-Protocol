package com.quogle.lavarise.sokoban;

import java.util.HashSet;
import java.util.Set;

public class Entity {
    private int x, y;
    private final Set<Property> properties = new HashSet<>();
    private final EntityType type;

    // Overrides
    private boolean pushableOverride = false; // e.g., snail on ice
    private boolean hasPushableOverride = false;
    private boolean frozen = false;

    public Entity(int x, int y, EntityType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    // Position getters/setters
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }

    public EntityType getType() { return type; }

    // Property management
    public boolean hasProperty(Property p) { return properties.contains(p); }
    public void addProperty(Property p) { properties.add(p); }
    public void removeProperty(Property p) { properties.remove(p); }
    public Set<Property> getProperties() { return properties; }

    // Pushable logic
    public void setPushableOverride(boolean pushable) {
        this.pushableOverride = pushable;
        this.hasPushableOverride = true;
    }

    public void clearPushableOverride() {
        this.hasPushableOverride = false;
    }

    public boolean isPushable() {
        return hasPushableOverride ? pushableOverride : getType().isPushable();
    }

    // Frozen logic
    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }
}
