package com.quogle.lavarise.sokoban.Entities;

import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationAssets;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Level.Level;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Collections;
import java.util.List;
import java.util.Set;


public class Cursor extends Entity {

    private final Level level;
    private List<Tile> selectableTiles;
    private int index = 0;

    public Cursor(Level level, EntityType type, AnimationManager animManager) {
        super(0, 0, type, animManager);
        this.level = level;
        refreshSelectableTiles();
        snapToCurrent();
        initAnimations();
    }

    // Update list of tiles with the SELECTABLE property
    public void refreshSelectableTiles() {
        List<Tile> oldSelectable = selectableTiles;
        selectableTiles = level.getTilesWithProperty(Property.SELECTABLE);

        if (selectableTiles.isEmpty()) return;

        if (oldSelectable != null && !oldSelectable.isEmpty()) {
            Tile oldTile = oldSelectable.get(index);
            int newIndex = selectableTiles.indexOf(oldTile);
            index = newIndex != -1 ? newIndex : 0; // if old tile still exists, keep index
        } else {
            index = 0;
        }

        snapToCurrent();
    }

    public void setSelectedTile(Tile tile) {
        this.index = selectableTiles.indexOf(tile);
        snapToCurrent();
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

        getAnimationManager().add("DEFAULT", AnimationAssets.CURSOR, 40, true);
    }
}