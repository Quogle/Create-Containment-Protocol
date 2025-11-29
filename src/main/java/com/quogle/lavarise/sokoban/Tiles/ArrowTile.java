package com.quogle.lavarise.sokoban.Tiles;

import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Level.Level;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ArrowTile extends Tile implements Rotatable {
    public Direction direction;
    private ResourceLocation[] frames;
    private int currentFrame = 0;

    public ArrowTile(int x, int y, Direction dir, Level level) {
        super(x, y, level);
        this.direction = dir;
        this.setType(TileType.ARROW);
        updateFrames();
        setCanRotate(true);
    }

    public void toggleFrame() { currentFrame = 1 - currentFrame; }
    public ResourceLocation getCurrentFrame() { return frames[currentFrame]; }

    @Override
    public Direction getDirection() { return direction; }

    @Override
    public boolean canRotate() {
        return true;
    }

    @Override
    public void rotateClockwise() {
        direction = switch (direction) {
            case UP -> Direction.RIGHT;
            case RIGHT -> Direction.DOWN;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
        };
        updateFrames();
    }

    @Override
    public ResourceLocation getPreview(Direction dir) {
        return switch (dir) {
            case UP -> Assets.ARROW_UP1;
            case DOWN -> Assets.ARROW_DOWN1;
            case LEFT -> Assets.ARROW_LEFT1;
            case RIGHT -> Assets.ARROW_RIGHT1;
        };
    }

    private void updateFrames() {
        frames = switch (direction) {
            case RIGHT -> new ResourceLocation[]{ Assets.ARROW_RIGHT1, Assets.ARROW_RIGHT2 };
            case LEFT  -> new ResourceLocation[]{ Assets.ARROW_LEFT1,  Assets.ARROW_LEFT2  };
            case UP    -> new ResourceLocation[]{ Assets.ARROW_UP1,    Assets.ARROW_UP2    };
            case DOWN  -> new ResourceLocation[]{ Assets.ARROW_DOWN1,  Assets.ARROW_DOWN2  };
        };
    }

    public List<Property> collectTransferableProperties() {
        List<Property> result = new ArrayList<>();
        for (Property p : this.getProperties()) {
            if (p.isTransferable())
                result.add(p);
        }
        return result;
    }
}
