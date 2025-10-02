package com.quogle.lavarise;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class LavaRisingHandler {

    private static int lavaHeight = 64;
    private static int tickCounter = 0;
    private static final int TICKS_PER_LAYER = 20 * 10; // 10 seconds per layer
    private static final int SIZE = 401; // SIZExSIZE square
    private static final int TOTAL_BLOCKS = SIZE * SIZE; // 40,401 blocks
    private static final int BLOCKS_PER_TICK = TOTAL_BLOCKS / TICKS_PER_LAYER + 1;
    private static boolean isRising = false; //

    //spiral step index (0 to TOTAL_BLOCKS-1)
    private static int spiralIndex = 0;
    private static ServerLevel overworld;
    private static final MutableBlockPos pos = new MutableBlockPos();

    public static void startRising() {
        if (isRising) return;
        isRising = true;
        lavaHeight = 64;
        spiralIndex = 0;
        tickCounter = 0;
        assert ServerLifecycleHooks.getCurrentServer() != null;
        overworld = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!isRising || overworld == null) return;
        tickCounter++;
        if (tickCounter >= TICKS_PER_LAYER) {
            tickCounter = 0;
            lavaHeight++;
            spiralIndex = 0; // Reset spiral for new layer
            assert ServerLifecycleHooks.getCurrentServer() != null;
            overworld = ServerLifecycleHooks.getCurrentServer().getLevel(Level.OVERWORLD);
            if (overworld == null) return;
        }

        // Place blocks in spiral this tick
        for (int i = 0; i < BLOCKS_PER_TICK && spiralIndex < TOTAL_BLOCKS; i++, spiralIndex++) {
            // Get spiral coordinates relative to center (0,0)
            int[] coords = getSpiralCoordinates(spiralIndex);
            int x = coords[0];
            int z = coords[1];

            pos.set(x, lavaHeight, z);
            BlockState currentState = overworld.getBlockState(pos);
            if (currentState.isAir()) {
                overworld.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
            }
        }
    }


     //Returns (x, z) coordinates for the current block in a square spiral
     //centered at (0,0), covering SIZE x SIZE area.
    private static int[] getSpiralCoordinates(int n) {

        if (n == 0) return new int[] {0, 0};

        int layer = 0;
        int maxInLayer = 1;

        // Find which layer the index belongs to
        while (maxInLayer <= n) {
            layer++;
            maxInLayer += 8 * layer;
        }

        int prevMax = maxInLayer - 8 * layer;
        int offset = n - prevMax;

        // Side length of current square ring (excluding corners)
        int sideLen = layer * 2;

        int x = 0, z = 0;

        //divide offset into side and position for each side
        int side = offset / sideLen; // 0 to 3
        int posOnSide = offset % sideLen;

        z = switch (side) {
            case 0 -> {
                x = layer;
                yield -layer + 1 + posOnSide;
            }
            case 1 -> {
                x = layer - 1 - posOnSide;
                yield layer;
            }
            case 2 -> {
                x = -layer;
                yield layer - 1 - posOnSide;
            }
            case 3 -> {
                x = -layer + 1 + posOnSide;
                yield -layer;
            }
            default -> z;
        };

        return new int[] {x, z};
    }
}
