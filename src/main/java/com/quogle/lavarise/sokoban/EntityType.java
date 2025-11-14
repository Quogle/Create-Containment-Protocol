package com.quogle.lavarise.sokoban;

import net.minecraft.resources.ResourceLocation;

public enum EntityType implements Rotatable{

    BOX(Assets.BOX, true, true, false),        // solid, pushable
    PLAYER(Assets.PLAYER, true, false, false), // solid, not pushable
    SNAIL(Assets.SNAIL_DOWN, true, false, true),
    CURSOR(Assets.CURSOR, false, false, false);

    private final ResourceLocation texture;
    private final boolean solid;
    private final boolean pushable;
    private final boolean rotatable;

    EntityType(ResourceLocation texture, boolean solid, boolean pushable, boolean rotatable) {
        this.texture = texture;
        this.solid = solid;
        this.pushable = pushable;
        this.rotatable = rotatable;
    }

    public ResourceLocation getTexture() { return texture; }
    public boolean isSolid() { return solid; }
    public boolean isPushable() { return pushable; }
    @Override
    public boolean canRotate() {
        return rotatable;
    }
    @Override
    public void rotateClockwise() {

    }

    public ResourceLocation getPreview(Direction dir) {
        // For rotatable tiles like ARROW, you could return different textures based on direction
        if (this == SNAIL) {
            return switch (dir) {
                case UP -> Assets.SNAIL_UP;
                case DOWN -> Assets.SNAIL_DOWN;
                case LEFT -> Assets.SNAIL_LEFT;
                case RIGHT -> Assets.SNAIL_RIGHT;
            };
        }
        // All other tiles just return their default texture
        return this.texture;
    }
}
