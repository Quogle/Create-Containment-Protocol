package com.quogle.lavarise.sokoban;

import net.minecraft.resources.ResourceLocation;

public enum Property {
    SELECTABLE(null),
    ICE(Assets.ICE_PREV),       // preview texture for editor
    FIRE(Assets.FIRE_PREV),
    WATER(Assets.WATER_PREV),
    ROTATE(Assets.ROTATE_PREV),
    NONE(null);

    private final ResourceLocation previewTexture;

    Property(ResourceLocation previewTexture) {
        this.previewTexture = previewTexture;
    }

    public ResourceLocation getPreviewTexture() {
        return previewTexture;
    }

    public boolean isTransferable() {
        return this != SELECTABLE;
    }


    public TileType toTileType() {
        return switch (this) {
            case FIRE  -> TileType.FIRE;
            case ICE   -> TileType.ICE;
            case WATER -> TileType.WATER;
            case ROTATE -> TileType.ROTATE;
            default    -> TileType.BLANK;
        };
    }


}

