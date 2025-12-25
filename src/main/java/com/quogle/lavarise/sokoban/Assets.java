package com.quogle.lavarise.sokoban;

import com.quogle.lavarise.LavaRise;
import net.minecraft.resources.ResourceLocation;

import static com.quogle.lavarise.sokoban.Direction.*;

public class Assets {

    // TILES
    public static final ResourceLocation WALL   = rl("textures/gui/sokoban/tiles/wall.png");
    public static final ResourceLocation FLOOR  = rl("textures/gui/sokoban/tiles/floor.png");
    public static final ResourceLocation CRACKED  = rl("textures/gui/sokoban/tiles/cracked.png");
    public static final ResourceLocation VOID   = rl("textures/gui/sokoban/tiles/void.png");
    public static final ResourceLocation IMPRINT   = rl("textures/gui/sokoban/tiles/imprint.png");
    public static final ResourceLocation HP     = rl("textures/gui/sokoban/tiles/hp.png");
    public static final ResourceLocation BLANK  = rl("textures/gui/sokoban/tiles/blank.png");
    public static final ResourceLocation FIRE   = rl("textures/gui/sokoban/tiles/fire.png");
    public static final ResourceLocation ROTATE   = rl("textures/gui/sokoban/tiles/rotate.png");
    public static final ResourceLocation ICE    = rl("textures/gui/sokoban/tiles/ice.png");
    public static final ResourceLocation WATER  = rl("textures/gui/sokoban/tiles/water.png");
    public static final ResourceLocation FL     = rl("textures/gui/sokoban/tiles/fl.png");
    public static final ResourceLocation NUMBER = rl("textures/gui/sokoban/tiles/number.png");
    public static final ResourceLocation BASIC  = rl("textures/gui/sokoban/tiles/basic.png");

    // ARROW TILES
    public static final ResourceLocation ARROW_RIGHT1 = rl("textures/gui/sokoban/tiles/arrow_right1.png");
    public static final ResourceLocation ARROW_RIGHT2 = rl("textures/gui/sokoban/tiles/arrow_right2.png");
    public static final ResourceLocation ARROW_LEFT1  = rl("textures/gui/sokoban/tiles/arrow_left1.png");
    public static final ResourceLocation ARROW_LEFT2  = rl("textures/gui/sokoban/tiles/arrow_left2.png");
    public static final ResourceLocation ARROW_UP1    = rl("textures/gui/sokoban/tiles/arrow_up1.png");
    public static final ResourceLocation ARROW_UP2    = rl("textures/gui/sokoban/tiles/arrow_up2.png");
    public static final ResourceLocation ARROW_DOWN1  = rl("textures/gui/sokoban/tiles/arrow_down1.png");
    public static final ResourceLocation ARROW_DOWN2  = rl("textures/gui/sokoban/tiles/arrow_down2.png");
    public static final ResourceLocation EXIT = rl("textures/gui/sokoban/tiles/exit.png");;


    // ENTITIES
    public static ResourceLocation PLAYER       = rl("textures/gui/sokoban/player/daco_front1.png");
    public static final ResourceLocation BOX    = rl("textures/gui/sokoban/entity/box.png");

    public static final ResourceLocation SNAIL_UP    = rl("textures/gui/sokoban/entity/snail_back1.png");
    public static final ResourceLocation SNAIL_DOWN    = rl("textures/gui/sokoban/entity/snail_front1.png");
    public static final ResourceLocation SNAIL_LEFT    = rl("textures/gui/sokoban/entity/snail_left1.png");
    public static final ResourceLocation SNAIL_RIGHT    = rl("textures/gui/sokoban/entity/snail_right1.png");

    public static ResourceLocation MOLE       = rl("textures/gui/sokoban/entity/mole/mole_idle2.png");

    public static final ResourceLocation CURSOR = rl("textures/gui/sokoban/entity/cursor_1.png");

    //EDITOR
    public static final ResourceLocation FIRE_PREV   = rl("textures/gui/sokoban/tiles/fire_prev.png");
    public static final ResourceLocation ROTATE_PREV   = rl("textures/gui/sokoban/tiles/rotate_prev.png");
    public static final ResourceLocation ICE_PREV    = rl("textures/gui/sokoban/tiles/ice_prev.png");
    public static final ResourceLocation WATER_PREV  = rl("textures/gui/sokoban/tiles/water_prev.png");

    //MISC
    public static final ResourceLocation ROTATE_OVERLAY = rl("textures/gui/sokoban/tiles/rotate_overlay.png");

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, path);
    }
}
