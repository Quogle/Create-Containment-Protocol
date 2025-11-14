package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationAssets;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.Entities.enums.SnailState;
import com.quogle.lavarise.sokoban.Entity;
import com.quogle.lavarise.sokoban.EntityType;
import com.quogle.lavarise.sokoban.Level.Level;
import com.quogle.lavarise.sokoban.Property;
import com.quogle.lavarise.sokoban.Tile;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Collections;
import java.util.List;
import java.util.Set;


public class Cursor extends Entity {

    private final Level level;
    private List<Tile> selectableTiles;
    private int index = 0;
    private final AnimationManager animManager = new AnimationManager();

    public Cursor(Level level, EntityType type) {
        super(0, 0, type);
        this.level = level;
        refreshSelectableTiles();
        snapToCurrent();
        initAnimations();
    }

    // Update list of tiles with the SELECTABLE property
    public void refreshSelectableTiles() {
        selectableTiles = level.getTilesWithProperty(Property.SELECTABLE);
    }

    // Move cursor to the next selectable tile
    public void cycle() {
        if (selectableTiles == null || selectableTiles.isEmpty()) return;
        index = (index + 1) % selectableTiles.size();
        snapToCurrent();
    }

    private void snapToCurrent() {
        if (selectableTiles == null || selectableTiles.isEmpty()) return;
        Tile t = selectableTiles.get(index);
        setPosition(t.getX(), t.getY());
    }

    public Tile getSelectedTile() {
        if (selectableTiles.isEmpty()) return null;
        return selectableTiles.get(index);
    }
    public Set<Property> getProperties() {
        Tile selected = getSelectedTile();
        return selected != null ? selected.getProperties() : Collections.emptySet();
    }

    private void initAnimations() {
        animManager.add("cursor", AnimationAssets.CURSOR, 40, true);
    }

    public void render(GuiGraphics guiGraphics, int offsetX, int offsetY, int tileSize) {
        // Tick the snail's animation
        animManager.tickAll();

        Animation anim = animManager.get("cursor");
        guiGraphics.blit(anim.getCurrentFrame(),
                offsetX + getX() * tileSize,
                offsetY + getY() * tileSize,
                0, 0, tileSize, tileSize,
                tileSize, tileSize);
    }
}