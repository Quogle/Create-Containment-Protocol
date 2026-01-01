package com.quogle.lavarise.client.sokoban;

import com.mojang.blaze3d.systems.RenderSystem;
import com.quogle.lavarise.client.sokoban.Animations.Animation;
import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.Cursor;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import com.quogle.lavarise.sokoban.Level.*;
import com.quogle.lavarise.sokoban.Tiles.ArrowTile;
import com.quogle.lavarise.sokoban.Tiles.ButtonTile;
import com.quogle.lavarise.sokoban.Tiles.ExitTile;
import com.quogle.lavarise.sokoban.Tiles.FloorNumTile;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.quogle.lavarise.sokoban.Entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class SokobanScreen extends Screen {
    private final int tileSize = 64;
    private Player player;
    public Level currentLevel;
    private Cursor cursor;

    private boolean editorMode = false;
    private EditorController editorController;
    private EditorCursor editorCursor;
    private boolean isZHeld = false;

    private AnimationManager animManager;

    //track held keys for free move
    private final Set<Integer> keysHeld = new HashSet<>();

    public SokobanScreen() {
        super(Component.literal("Sokoban"));
        loadLevel();
    }

    public void loadLevel() {
        currentLevel = LevelBuilder.createExampleLevel();
        editorController = new EditorController(currentLevel);
        editorMode = false;
        animManager = new AnimationManager(currentLevel);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        keysHeld.add(keyCode);

        // Toggle editor mode
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT) {
            editorMode = !editorMode;
            if (editorMode) enableEditorMode();
            return true;
        }

        // Editor controls
        if (editorMode) {
            boolean isZHeld = keysHeld.contains(GLFW.GLFW_KEY_Z);
            boolean isCHeld = keysHeld.contains(GLFW.GLFW_KEY_C);

            Level loaded = editorController.handleKeyPress(keyCode, isZHeld, isCHeld);
            if (loaded != null) {
                currentLevel = loaded;
                editorController = new EditorController(currentLevel);
            }
            return true;
        }

        // Tile-based movement for all players
        if (!currentLevel.isFreemove()) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_LEFT -> movePlayer(-1, 0);
                case GLFW.GLFW_KEY_RIGHT -> movePlayer(1, 0);
                case GLFW.GLFW_KEY_UP -> movePlayer(0, -1);
                case GLFW.GLFW_KEY_DOWN -> movePlayer(0, 1);
                case GLFW.GLFW_KEY_X -> currentLevel.getEntities().stream()
                        .filter(e -> e instanceof Cursor)
                        .map(e -> (Cursor) e)
                        .forEach(Cursor::cycle);
                case GLFW.GLFW_KEY_Z -> {
                    // Place properties in front for all players using all cursors
                    for (Entity e : currentLevel.getEntities()) {
                        if (e instanceof Player p) {
                            currentLevel.getEntities().stream()
                                    .filter(ent -> ent instanceof Cursor)
                                    .map(ent -> (Cursor) ent)
                                    .forEach(c -> p.placePropertiesInFront(currentLevel, c.getSelectedTile()));
                        }
                    }
                }
                case GLFW.GLFW_KEY_R -> currentLevel.resetLevel();
                case GLFW.GLFW_KEY_F -> currentLevel.advanceFloor();
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        keysHeld.remove(keyCode);
        // nothing extra needed since handleKeyPress checks keysHeld every tick
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
                    default -> Assets.VOID;
                };

                if (tile instanceof ArrowTile arrowTile) tileTexture = arrowTile.getCurrentFrame();
                if (tile instanceof ExitTile exitTile) tileTexture = exitTile.getSprite();
                if (tile instanceof ButtonTile buttonTile) tileTexture = buttonTile.getSprite();

                if (tile instanceof FloorNumTile numTile) {
                    ResourceLocation bg = numTile.getBackground();
                    ResourceLocation left = numTile.getLeftDigit();
                    ResourceLocation right = numTile.getRightDigit();

                    guiGraphics.blit(bg, drawX, drawY, 0, 0, dynamicTileSize, dynamicTileSize, dynamicTileSize, dynamicTileSize);
                    guiGraphics.blit(left, drawX, drawY, 0, 0, dynamicTileSize, dynamicTileSize, dynamicTileSize, dynamicTileSize);
                    guiGraphics.blit(right, drawX, drawY, 0, 0, dynamicTileSize, dynamicTileSize, dynamicTileSize, dynamicTileSize);
                } else {
                    guiGraphics.blit(tileTexture, drawX, drawY, 0, 0, dynamicTileSize, dynamicTileSize, dynamicTileSize, dynamicTileSize);
                }


                //Tile shader color
                if (tile.hasProperty(Anomaly.FIRE)) RenderSystem.setShaderColor(1f, 0f, 0f, 1f);
                else if (tile.hasProperty(Anomaly.ICE)) RenderSystem.setShaderColor(0.5f, 0.9f, 1.0f, 1f);
                else if (tile.hasProperty(Anomaly.WATER)) RenderSystem.setShaderColor(0.5f, 0.5f, 1f, 1f);
                else if (tile.hasProperty(Anomaly.ROTATE)) RenderSystem.setShaderColor(1.0f, 0.53f, 1.0f, 1f);
                else RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
        }

        //sort entities based on their rendering layer
        List<Entity> sorted = new ArrayList<>(currentLevel.getEntities());
        sorted.sort(Comparator.comparingInt(Entity::getRenderLayer));



        //render entities
        for (Entity e : sorted) {
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
            Anomaly previewProperty = editorController.getSelectedProperty();
            return previewProperty.getPreviewTexture();
        }
        else if (editorController.getActiveTab() == EditorTab.MISC) {
            Object misc = editorController.getSelectedMisc();
            if (misc instanceof EntityType entityType) {
                Direction dir = editorController.getSelectedEntityDirection();
                return entityType.getPreview(dir);
            } else if (misc instanceof TileType tileType) {
                Direction dir = editorController.getSelectedTileDirection();
                return tileType.getPreview(dir); // if you have a preview method for tiles
            }
        }
        return null;
    }

    //Tile based movement helper
    private void movePlayer(int dx, int dy) {
        for (Entity e : currentLevel.getEntities()) {
            if (e instanceof Player p) {
                p.move(dx, dy, currentLevel);
                currentLevel.tryEnterExit(p);
                currentLevel.enterVoid(p);
            }
        }
    }


    public void enableEditorMode () {
        editorMode = true;
        editorCursor = new EditorCursor(currentLevel, editorController, EntityType.CURSOR);
    }

    @Override
    public void tick() {
        super.tick();

        if (currentLevel == null) return;

        currentLevel.pressButton();
        currentLevel.activateExit();
        currentLevel.getEntities().stream()
                .filter(e -> e instanceof Player)
                .map(e -> (Player) e)
                .forEach(p -> currentLevel.tryEnterExit(p));

        // Tick entities ONCE per game tick (20 TPS)
        for (Entity e : currentLevel.getEntities()) {
            e.tickAnimations();
            e.update(currentLevel);
        }

        // Free-move logic (tick-based, not render-based)
        if (currentLevel.isFreemove() && player != null) {
            float dx = 0, dy = 0;

            if (keysHeld.contains(GLFW.GLFW_KEY_LEFT)) dx -= 1;
            if (keysHeld.contains(GLFW.GLFW_KEY_RIGHT)) dx += 1;
            if (keysHeld.contains(GLFW.GLFW_KEY_UP)) dy -= 1;
            if (keysHeld.contains(GLFW.GLFW_KEY_DOWN)) dy += 1;

            if (dx != 0 || dy != 0) {
                float speedPerTick = 0.1f; // tune this value
                player.moveFree(dx, dy, currentLevel, speedPerTick);
            }
        }
    }
}
