package com.quogle.lavarise.sokoban;

import net.minecraft.resources.ResourceLocation;

public enum TileType implements Rotatable {

    FLOOR(Assets.FLOOR,false, false),
    CRACKED(Assets.CRACKED,false, false),
    WALL(Assets.WALL,true, false),
    VOID(Assets.VOID,false, false),
    EXIT(Assets.EXIT_ACTIVE,false, false),
    BUTTON(Assets.BUTTON,false, false),
    IMPRINT(Assets.IMPRINT,false, false),
    ARROW(Assets.ARROW_RIGHT1,false, true),
    HP(Assets.HP,false, false),
    BASIC(Assets.BASIC,false, false),
    NUMBER(Assets.NUMBER,false, false),
    FL(Assets.FL,false, false),
    BLANK(Assets.BLANK,false, false),
    FIRE(Assets.FIRE,false, false),
    ICE(Assets.ICE,false, false),
    WATER(Assets.WATER,false, false),
    ROTATE(Assets.WATER,false, false);

    private final ResourceLocation texture;
    private final boolean solid;
    private final boolean rotatable;

    TileType(ResourceLocation texture, boolean solid, boolean rotatable) {
        this.texture = texture;
        this.solid = solid;
        this.rotatable = rotatable;
    }

    public ResourceLocation getTexture() { return texture; }
    public boolean isSolid() { return solid; }

    @Override
    public boolean canRotate() {
        return rotatable;
    }

    @Override
    public void rotateClockwise() {

    }

    public ResourceLocation getPreview(Direction dir) {
        // For rotatable tiles like ARROW, you could return different textures based on direction
        if (this == ARROW) {
            return switch (dir) {
                case NONE -> null;
                case UP -> Assets.ARROW_UP1;
                case DOWN -> Assets.ARROW_DOWN1;
                case LEFT -> Assets.ARROW_LEFT1;
                case RIGHT -> Assets.ARROW_RIGHT1;
            };
        }
        // All other tiles just return their default texture
        return this.texture;
    }
}

