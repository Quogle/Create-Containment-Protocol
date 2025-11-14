package com.quogle.lavarise.sokoban.Level;

import java.util.List;
import java.util.ArrayList;

public class LevelManager {
    private final List<Level> levels = new ArrayList<>();
    private int currentLevelIndex = 0;

    public void addLevel(Level level) {
        levels.add(level);
    }

    public Level getCurrentLevel() {
        return levels.get(currentLevelIndex);
    }

    public void nextLevel() {
        if (currentLevelIndex < levels.size() - 1) currentLevelIndex++;
    }

    public void previousLevel() {
        if (currentLevelIndex > 0) currentLevelIndex--;
    }
}
