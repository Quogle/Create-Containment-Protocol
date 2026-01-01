package com.quogle.lavarise.sokoban.Level;

import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import org.lwjgl.glfw.GLFW;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import org.lwjgl.glfw.GLFW;

public class EditorController {
    private final Level level;
    private EditorTab activeTab = EditorTab.TILE; // default
    private final EditorCursor editorCursor;
    private TileType selectedTileType = TileType.FLOOR;
    private Anomaly selectedProperty = Anomaly.FIRE;
    private EntityType selectedEntityType = EntityType.PLAYER;
    private Object selectedMisc = EntityType.CURSOR;// default
    private Direction selectedTileDirection = Direction.RIGHT;
    private Direction selectedEntityDirection = Direction.DOWN;// default for preview

    public EditorController(Level level) {
        this.level = level;
        this.editorCursor = new EditorCursor(level, this, EntityType.CURSOR);
    }

    public EditorTab getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(EditorTab tab) {
        this.activeTab = tab;
        switch (tab) {
            case TILE -> selectedTileType = TileType.FLOOR;  // first tile
            case ENTITY -> selectedEntityType = EntityType.PLAYER; // first entity
        }
    }

    public EditorCursor getEditorCursor() {
        return editorCursor;
    }

    public TileType getSelectedTileType() {
        return selectedTileType;
    }

    public Direction getSelectedTileDirection() {
        return selectedTileDirection;
    }

    public EntityType getSelectedEntityType() {
        return selectedEntityType;
    }

    public Object getSelectedMisc() {
        return selectedMisc;
    }

    public Anomaly getSelectedProperty() {
        return selectedProperty;
    }

    public Direction getSelectedEntityDirection() {
        return selectedEntityDirection;
    }

    public void selectEditorItem(int number) {
        switch (activeTab) {
            case TILE -> selectedTileType = switch (number) {
                case 1 -> TileType.FLOOR;
                case 2 -> TileType.WALL;
                case 3 -> TileType.VOID;
                case 4 -> TileType.EXIT;
                case 5 -> TileType.BUTTON;
                case 6 -> TileType.ARROW;
                case 7 -> TileType.CRACKED;
                case 8 -> TileType.IMPRINT;
                default -> selectedTileType;
            };
            case ENTITY -> selectedEntityType = switch (number) {
                case 1 -> EntityType.PLAYER;
                case 2 -> EntityType.BOX;
                case 3 -> EntityType.SNAIL;
                case 4 -> EntityType.MOLE;
                default -> selectedEntityType;
            };
            case PROPERTY -> selectedProperty = switch (number) {
                case 1 -> Anomaly.FIRE;
                case 2 -> Anomaly.ICE;
                case 3 -> Anomaly.ROTATE;
                case 4 -> Anomaly.WATER;
                default -> selectedProperty;
            };
            case MISC -> selectedMisc = switch (number) {
                case 1 -> EntityType.CURSOR;
                case 2 -> TileType.NUMBER;
                default -> selectedMisc;
            };
        }
    }

    public void placeSelectedItem() {
        if (activeTab == EditorTab.TILE) {
            editorCursor.placeTile(selectedTileType, selectedTileDirection);
        } else if (activeTab == EditorTab.ENTITY) {
            editorCursor.clearEntities();
            editorCursor.placeEntity(selectedEntityType, selectedEntityDirection);
        } else if (activeTab == EditorTab.PROPERTY) {
            editorCursor.clearProperties();
            editorCursor.addProperty(selectedProperty);
        } else if (activeTab == EditorTab.MISC) {
            if (selectedMisc instanceof EntityType entityType) {
                editorCursor.clearEntities();
                editorCursor.placeEntity(entityType, selectedEntityDirection);
            } else if (selectedMisc instanceof TileType tileType) {
                editorCursor.placeTile(tileType, selectedTileDirection);
            }
        }
    }

    private void rotateSelected() {
        if (activeTab == EditorTab.TILE && selectedTileType.canRotate()) {
            selectedTileDirection = selectedTileDirection.nextClockwise();
        }
        if (activeTab == EditorTab.ENTITY && selectedEntityType.canRotate()) {
            selectedEntityDirection = selectedEntityDirection.nextClockwise();
        }
    }

    private void deleteSelected() {
        editorCursor.clearEntities();
    }

    public Level handleKeyPress(int keyCode, boolean isZHeld, boolean isCHeld) {
        Level loadedLevel = null;

        switch (keyCode) {
            case GLFW.GLFW_KEY_1 -> setActiveTab(EditorTab.TILE);
            case GLFW.GLFW_KEY_2 -> setActiveTab(EditorTab.ENTITY);
            case GLFW.GLFW_KEY_3 -> setActiveTab(EditorTab.PROPERTY);
            case GLFW.GLFW_KEY_4 -> setActiveTab(EditorTab.MISC);

            case GLFW.GLFW_KEY_Q -> selectEditorItem(1);
            case GLFW.GLFW_KEY_W -> selectEditorItem(2);
            case GLFW.GLFW_KEY_E -> selectEditorItem(3);
            case GLFW.GLFW_KEY_R -> selectEditorItem(4);
            case GLFW.GLFW_KEY_T -> selectEditorItem(5);
            case GLFW.GLFW_KEY_A -> selectEditorItem(6);
            case GLFW.GLFW_KEY_S -> selectEditorItem(7);

            case GLFW.GLFW_KEY_Z -> placeSelectedItem();
            case GLFW.GLFW_KEY_X -> rotateSelected();
            case GLFW.GLFW_KEY_C -> deleteSelected();

            case GLFW.GLFW_KEY_LEFT -> moveCursor(-1, 0, isZHeld, isCHeld);
            case GLFW.GLFW_KEY_RIGHT -> moveCursor(1, 0, isZHeld, isCHeld);
            case GLFW.GLFW_KEY_UP -> moveCursor(0, -1, isZHeld, isCHeld);
            case GLFW.GLFW_KEY_DOWN -> moveCursor(0, 1, isZHeld, isCHeld);

            case GLFW.GLFW_KEY_K -> saveLevel("saved_level.json");
            case GLFW.GLFW_KEY_L -> loadedLevel = loadLevel("saved_level.json");
        }

        return loadedLevel;
    }

    public void moveCursor(int dx, int dy, boolean isZHeld, boolean isCHeld) {
        editorCursor.move(dx, dy, false); // move without auto-place

        if (isZHeld) placeSelectedItem();
        if (isCHeld) deleteSelected();
    }

    public void saveLevel(String filename) {
        if (level != null) {
            level.saveToFile(filename);
            System.out.println("Level saved to " + filename);
        }
    }

    public Level loadLevel(String filename) {
        Level loaded = Level.loadFromFile(filename);
        if (loaded != null) {
            System.out.println("Level loaded from " + filename);
            return loaded;
        }
        return null;
    }
}
