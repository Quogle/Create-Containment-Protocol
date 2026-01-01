package com.quogle.lavarise.client.sokoban.Animations;

import com.quogle.lavarise.LavaRise;
import net.minecraft.resources.ResourceLocation;

public class AnimationAssets {

    //PLAYER
    public static final ResourceLocation[] IDLE_FRONT = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_front1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_front2.png")
    };

    public static final ResourceLocation[] IDLE_BACK = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_back1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_back2.png")
    };

    public static final ResourceLocation[] IDLE_LEFT = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_left1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_left2.png")
    };

    public static final ResourceLocation[] IDLE_RIGHT = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_right1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_right2.png")
    };

    public static final ResourceLocation[] FRONT_PUSH = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_front_push1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_front_push2.png")
    };
    public static final ResourceLocation[] BACK_PUSH = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_back_push1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_back_push2.png")
    };
    public static final ResourceLocation[] RIGHT_PUSH = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_right_push1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_right_push2.png")
    };
    public static final ResourceLocation[] LEFT_PUSH = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_left_push1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/player/daco_left_push2.png")
    };

    public static final int[] PUSH_DURATIONS = new int[] {
            5,
            10
    };

    //ENEMIES
    public static final ResourceLocation[] SNAIL_LEFT = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/snail_left1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/snail_left2.png")
    };
    public static final ResourceLocation[] SNAIL_RIGHT = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/snail_right1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/snail_right2.png")
    };
    public static final ResourceLocation[] SNAIL_UP = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/snail_back1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/snail_back2.png")
    };
    public static final ResourceLocation[] SNAIL_DOWN = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/snail_front1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/snail_front2.png")
    };


    public static final ResourceLocation[] MOLE_IDLE = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/mole/mole_idle2.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/mole/mole_idle1.png")
    };

    public static final ResourceLocation[] MOLE_BLOCK = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/mole/mole_bump1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/mole/mole_bump2.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/mole/mole_bump3.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/mole/mole_bump2.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/mole/mole_bump1.png"),
    };

    public static final int[] BUMP_DURATIONS = new int[] {
            2,
            2,
            10,
            2,
            2
    };

    //MISC
    public static final ResourceLocation[] CURSOR = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/cursor_1.png"),
            ResourceLocation.fromNamespaceAndPath(LavaRise.MOD_ID, "textures/gui/sokoban/entity/cursor_2.png")
    };
}
