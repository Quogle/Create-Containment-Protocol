package com.quogle.lavarise.sokoban.Entities;

import com.mojang.blaze3d.systems.RenderSystem;
import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.client.sokoban.Animations.StateMachine;
import com.quogle.lavarise.sokoban.AnimatableState;
import com.quogle.lavarise.sokoban.Direction;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Anomaly;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class Entity{
    private Direction direction = Direction.NONE;
    private int x, y;
    private int slideDx = 0;
    private int slideDy = 0;
    private final Set<Anomaly> properties = new HashSet<>();
    private final EntityType type;

    protected AnimationManager animationManager;
    // Only stateful entities override this
    protected StateMachine stateMachine = null;

    // Overrides
    private boolean pushableOverride = false;
    private boolean hasPushableOverride = false;
    private boolean solidOverride = false;
    private boolean hasSolidOverride = false;
    private boolean frozen = false;

    public Entity(int x, int y, EntityType type, AnimationManager animManager) {
        this.x = x;
        this.y = y;

        this.type = type;
        this.animationManager = animManager;
    }

    public ResourceLocation getPreview(Direction dir) {
        return null;
    }

    /** Return the animation key to render */
    public String getCurrentAnimationKey() {
        if (stateMachine != null)
            return stateMachine.getState().getAnimKey();
        return "DEFAULT";
    }


    public void render(GuiGraphics guiGraphics, int offsetX, int offsetY, int tileSize, Animation anim, float posX, float posY) {
        // Set color based on properties
        if (hasProperty(Anomaly.FIRE)) RenderSystem.setShaderColor(1f, 0f, 0f, 1f);
        else if (hasProperty(Anomaly.ICE)) RenderSystem.setShaderColor(0.5f, 0.9f, 1f, 1f);
        else if (hasProperty(Anomaly.WATER)) RenderSystem.setShaderColor(0.5f, 0.5f, 1f, 1f);
        else if (hasProperty(Anomaly.ROTATE)) RenderSystem.setShaderColor(1f, 0.53f, 1f, 1f);
        else RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        guiGraphics.blit(anim.getCurrentFrame(),
                (int)(posX * tileSize + offsetX),
                (int)(posY * tileSize + offsetY),
                0, 0, tileSize, tileSize, tileSize, tileSize);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f); // reset
    }
    public void tickAnimations() {
        animationManager.tickAll();

        if (stateMachine == null) return;

        AnimatableState state = stateMachine.getState();
        Animation anim = animationManager.get(state.getAnimKey());

        if (state.hasNextState() && anim.isFinished()) {
            AnimatableState next = state.getNextState();
            stateMachine.setState(next);
            animationManager.get(next.getAnimKey()).reset();
        }
    }

    // --- Position ---
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
    public void setSlideDirection(int dx, int dy) {
        slideDx = dx;
        slideDy = dy;
    }
    public int getSlideDx() { return slideDx; }
    public int getSlideDy() { return slideDy; }
    public void clearSlide() {
        slideDx = 0;
        slideDy = 0;
    }

    public EntityType getType() { return type; }
    public AnimationManager getAnimationManager() { return animationManager; }

    // --- Properties ---
    public boolean hasProperty(Anomaly p) { return properties.contains(p); }
    public void addProperty(Anomaly p) { properties.add(p); }
    public void removeProperty(Anomaly p) { properties.remove(p); }
    public Set<Anomaly> getProperties() { return properties; }
    public Direction getDirection() { return direction;}
    public void setDirection(Direction dir) { this.direction = dir != null ? dir : Direction.NONE; }
    public void clearProperties() { properties.removeIf(Anomaly::isTransferable); }

    public void update(Level level) {}


    // --- Pushable ---
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

    public void setSolidOverride(boolean solid) {
        this.solidOverride = solid;
        this.hasSolidOverride = true;
    }

    public void clearSolidOverride() {
        this.hasSolidOverride = false;
    }

    public boolean isSolid() {
        return hasSolidOverride ? solidOverride : getType().isSolid();
    }

    public boolean isStackable() {
        return false;
    }
    public int getRenderLayer() {
        return 1; // default layer for normal entities
    }


    // --- Frozen ---
    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }
}
