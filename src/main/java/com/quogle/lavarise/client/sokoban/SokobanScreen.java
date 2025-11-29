package com.quogle.lavarise.client.sokoban;

import com.mojang.blaze3d.systems.RenderSystem;
import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.Cursor;
import com.quogle.lavarise.sokoban.Level.*;
import com.quogle.lavarise.sokoban.Tiles.ArrowTile;
import com.quogle.lavarise.sokoban.Tiles.ExitTile;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.quogle.lavarise.sokoban.Entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class SokobanScreen extends Screen {
    private final int tileSize = 64;
    private Player player;
    public Level currentLevel;
    private Cursor cursor;

    private boolean editorMode = false;
    private EditorController editorController;
    private EditorCursor editorCursor;
    private boolean isZHeld = false;

    private final AnimationManager animManager = new AnimationManager();

    //track held keys for free move
    private final Set<Integer> keysHeld = new HashSet<>();

    public SokobanScreen() {
        super(Component.literal("Sokoban"));
        loadLevel();
    }

    public void loadLevel() {
        currentLevel = LevelBuilder.createExampleLevel();
        currentLevel.ensurePlayerExists();

        player = currentLevel.getPlayers().isEmpty() ? null : currentLevel.getPlayers().get(0);

        cursor = new Cursor(currentLevel, EntityType.CURSOR, animManager);
        currentLevel.addCursor(cursor);

        editorController = new EditorController(currentLevel);
        editorMode = false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        keysHeld.add(keyCode);

        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            editorMode = !editorMode;
            if (editorMode) enableEditorMode();
            return true;
        }

        if (editorMode) {
            if (keyCode == GLFW.GLFW_KEY_Z) {
                isZHeld = true;
                editorController.placeSelectedItem();
                return true;
            }

            Level loaded = editorController.handleKeyPress(keyCode, isZHeld);
            if (loaded != null) {
                currentLevel = loaded;
                player = currentLevel.getPlayers().isEmpty() ? null : currentLevel.getPlayers().get(0);
                editorController = new EditorController(currentLevel);
                cursor = new Cursor(currentLevel, EntityType.CURSOR, animManager);
                currentLevel.addCursor(cursor);
            }
            return true;
        }

        // Tile move mode
        if (!currentLevel.isFreemove() && player != null) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT -> movePlayer(-1, 0);
                case GLFW.GLFW_KEY_RIGHT -> movePlayer(1, 0);
                case GLFW.GLFW_KEY_UP -> movePlayer(0, -1);
                case GLFW.GLFW_KEY_DOWN -> movePlayer(0, 1);
                case GLFW.GLFW_KEY_X -> cursor.cycle();
                case GLFW.GLFW_KEY_Z -> player.placePropertiesInFront(currentLevel, cursor.getSelectedTile());
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        keysHeld.remove(keyCode);

        if (keyCode == GLFW.GLFW_KEY_Z) isZHeld = false;

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        int dynamicTileSize = guiGraphics.guiHeight() / 9;
        int offsetX = (guiGraphics.guiWidth() - currentLevel.getWidth() * dynamicTileSize) / 2;
        int offsetY = (guiGraphics.guiHeight() - currentLevel.getHeight() * dynamicTileSize) / 2;

        //black bg
        guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0xFF000000);

        //render tiles
        for (int y = 0; y < currentLevel.getHeight(); y++) {
            for (int x = 0; x < currentLevel.getWidth(); x++) {
                Tile tile = currentLevel.getTile(x, y);
                int drawX = offsetX + x * dynamicTileSize;
                int drawY = offsetY + y * dynamicTileSize;

                ResourceLocation tileTexture = switch (tile.getType()) {
                    case FLOOR -> Assets.FLOOR;
                    case CRACKED -> Assets.CRACKED;
                    case WALL -> Assets.WALL;
                    case IMPRINT -> Assets.IMPRINT;
                    case HP -> Assets.HP;
                    case BLANK -> Assets.BLANK;
                    case FIRE -> Assets.FIRE;
                    case ROTATE -> Assets.ROTATE;
                    case ICE -> Assets.ICE;
                    case WATER -> Assets.WATER;
                    case BASIC -> Assets.BASIC;
                    case FL -> Assets.FL;
                    case NUMBER -> Assets.NUMBER;
                    default -> Assets.VOID;
                };

                if (tile instanceof ArrowTile arrowTile) tileTexture = arrowTile.getCurrentFrame();
                if (tile instanceof ExitTile exitTile) tileTexture = exitTile.getSprite();

                //Tile shader color
                if (tile.hasProperty(Property.FIRE)) RenderSystem.setShaderColor(1f, 0f, 0f, 1f);
                else if (tile.hasProperty(Property.ICE)) RenderSystem.setShaderColor(0.5f, 0.9f, 1.0f, 1f);
                else if (tile.hasProperty(Property.WATER)) RenderSystem.setShaderColor(0.5f, 0.5f, 1f, 1f);
                else if (tile.hasProperty(Property.ROTATE)) RenderSystem.setShaderColor(1.0f, 0.53f, 1.0f, 1f);
                else RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

                guiGraphics.blit(tileTexture, drawX, drawY, 0, 0, dynamicTileSize, dynamicTileSize,
                        dynamicTileSize, dynamicTileSize);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
        }

        //player free move
        if (currentLevel.isFreemove() && player != null) {
            float dx = 0, dy = 0;
            if (keysHeld.contains(GLFW.GLFW_KEY_LEFT)) dx -= 1;
            if (keysHeld.contains(GLFW.GLFW_KEY_RIGHT)) dx += 1;
            if (keysHeld.contains(GLFW.GLFW_KEY_UP)) dy -= 1;
            if (keysHeld.contains(GLFW.GLFW_KEY_DOWN)) dy += 1;

            if (dx != 0 || dy != 0) {
                float deltaTime = 1 / 60f; // approximate, or pass actual delta from render
                player.moveFree(dx, dy, currentLevel, deltaTime);
            }
        }

        //render entities
        for (Entity e : currentLevel.getEntities()) {
            e.tickAnimations(); // advance animation frames

            Animation anim = e.getAnimationManager().get(e.getCurrentAnimationKey());

            float drawX = e.getX();
            float drawY = e.getY();

            if (e instanceof Player p && currentLevel.isFreemove()) {
                drawX = p.posX;
                drawY = p.posY;
            }

            e.render(guiGraphics, offsetX, offsetY, dynamicTileSize, anim, drawX, drawY);
        }

        if(editorMode) {
        EditorCursor ecursor = editorController.getEditorCursor();
        int drawX = offsetX + ecursor.getX() * dynamicTileSize;
        int drawY = offsetY + ecursor.getY() * dynamicTileSize;

        Tile tileUnderCursor = currentLevel.getTile(ecursor.getX(), ecursor.getY());
        ResourceLocation previewTexture = getResourceLocation();

        if (previewTexture != null) {
            guiGraphics.blit(previewTexture, drawX, drawY, 0, 0, dynamicTileSize, dynamicTileSize, dynamicTileSize, dynamicTileSize);
        }

        int borderSize = 2;
        guiGraphics.fill(drawX - borderSize, drawY - borderSize, drawX + dynamicTileSize + borderSize, drawY, 0xFFFFFF00);
        guiGraphics.fill(drawX - borderSize, drawY + dynamicTileSize, drawX + dynamicTileSize + borderSize, drawY + dynamicTileSize + borderSize, 0xFFFFFF00);
        guiGraphics.fill(drawX - borderSize, drawY, drawX, drawY + dynamicTileSize, 0xFFFFFF00);
        guiGraphics.fill(drawX + dynamicTileSize, drawY, drawX + dynamicTileSize + borderSize, drawY + dynamicTileSize, 0xFFFFFF00);

        // Reset color after each tile/entity
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }

    private @Nullable ResourceLocation getResourceLocation() {
        if (editorController.getActiveTab() == EditorTab.TILE) {
            TileType previewTile = editorController.getSelectedTileType();
            Direction dir = editorController.getSelectedTileDirection(); // direction set in editor
            return previewTile.getPreview(dir); // Rotatable tiles return rotated preview, others default
        } else if (editorController.getActiveTab() == EditorTab.ENTITY) {
            EntityType previewEntity = editorController.getSelectedEntityType();
            Direction dir = editorController.getSelectedEntityDirection();
            return previewEntity.getPreview(dir); // Rotatable entities return rotated preview, others default
        } else if (editorController.getActiveTab() == EditorTab.PROPERTY) {
            Property previewProperty = editorController.getSelectedProperty();
            return previewProperty.getPreviewTexture();
        }
        return null;
    }

    //Tile based movement helper
    private void movePlayer(int dx, int dy) {
        for (Player p : currentLevel.getPlayers()) {
            p.move(dx, dy, currentLevel);
            tryEnterExit(player);
            enterVoid(player);
            Tile t = currentLevel.getTile(p.getX(), p.getY());
            if (t.hasProperty(Property.ICE)) { /* TODO: slide logic */ }
        }
    }

    public void enableEditorMode () {
        editorMode = true;
        editorCursor = new EditorCursor(currentLevel, editorController, EntityType.CURSOR);
    }
    public void tryEnterExit(Player player) {
        Tile tile = currentLevel.getTile(player.getX(), player.getY());
        if (tile instanceof ExitTile) {
            // Increase floor count
            currentLevel.setCurrentFloor(currentLevel.getCurrentFloor() + 1);

            // Build filename for next floor
            String nextFile = currentLevel.getCurrentZone() + currentLevel.getCurrentFloor() + ".json";
            System.out.println("Loading next level: " + nextFile);
            Level nextLevel = Level.loadFromFile(nextFile);

            assert nextLevel != null;
            nextLevel.setCurrentZone(currentLevel.currentZone);
            nextLevel.setCurrentFloor(currentLevel.currentFloor);

            currentLevel = nextLevel;
            this.player = currentLevel.getPlayers().isEmpty() ? null : currentLevel.getPlayers().get(0);

            cursor = new Cursor(currentLevel, EntityType.CURSOR, animManager);
            currentLevel.addCursor(cursor);

            editorController = new EditorController(currentLevel);
        }
    }
    public void enterVoid(Player player) {
        Tile tile = currentLevel.getTile(player.getX(), player.getY());
        if (tile.getType() == TileType.VOID) {

            // Build filename for next floor
            String nextFile = currentLevel.getCurrentZone() + currentLevel.getCurrentFloor() + ".json";
            System.out.println("Loading next level: " + nextFile);
            Level nextLevel = Level.loadFromFile(nextFile);

            assert nextLevel != null;
            nextLevel.setCurrentZone(currentLevel.currentZone);
            nextLevel.setCurrentFloor(currentLevel.currentFloor);

            currentLevel = nextLevel;
            this.player = currentLevel.getPlayers().isEmpty() ? null : currentLevel.getPlayers().get(0);

            cursor = new Cursor(currentLevel, EntityType.CURSOR, animManager);
            currentLevel.addCursor(cursor);

            editorController = new EditorController(currentLevel);
        }
    }
}
