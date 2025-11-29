package com.quogle.lavarise.sokoban.Level;

import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.Box;
import com.quogle.lavarise.sokoban.Entities.Cursor;
import com.quogle.lavarise.sokoban.Entities.Player;
import com.quogle.lavarise.sokoban.Entities.Snail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quogle.lavarise.sokoban.Tiles.ArrowTile;
import com.quogle.lavarise.sokoban.Tiles.ExitTile;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Level {

    public int currentFloor = 0;
    public String currentZone = "A_";

    public boolean freemove = false;
    private final int width = 14;
    private final int height = 9;
    private final Tile[][] tiles;
    private final List<Player> players = new ArrayList<>();
    private List<Snail> snails = new ArrayList<>();
    private List<Box> boxes = new ArrayList<>();
    private List<Cursor> cursors = new ArrayList<>();

    public Level() {
        tiles = new Tile[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = new Tile(x, y, this);
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return tiles[y][x];
    }

    public void setTile(int x, int y, Tile tile) {
        tiles[y][x] = tile;
    }

    public Entity getEntity(int x, int y) {
        Tile t = getTile(x, y);
        return t != null ? t.getEntity() : null;
    }

    public List<Tile> getTilesWithProperty(Property property) {
        List<Tile> result = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y);
                if (t.hasProperty(property)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    public void ensurePlayerExists() {
        if (players.isEmpty()) {
            Player p = new Player(0, 0, new AnimationManager(), EntityType.PLAYER, this);
            addPlayer(p);
        }
    }

    public Tile getTileInDirection(Tile tile, Direction dir) {
        int x = tile.getX();
        int y = tile.getY();

        return switch (dir) {
            case UP -> getTile(x, y - 1);
            case DOWN -> getTile(x, y + 1);
            case LEFT -> getTile(x - 1, y);
            case RIGHT -> getTile(x + 1, y);
        };
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public String getCurrentZone() {
        return currentZone;
    }

    public void setCurrentFloor(int newFloor) {
        this.currentFloor = newFloor;
    }

    public void setCurrentZone(String newZone) {
        this.currentZone = newZone;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void addSnail(Snail s) {
        snails.add(s);
    }

    public Snail[] getSnails() {
        return snails.toArray(new Snail[0]);
    }

    public void addBox(Box b) {
        boxes.add(b);
    }

    public Box[] getBoxes() {
        return boxes.toArray(new Box[0]);
    }

    public void addCursor(Cursor c) {
        cursors.add(c);
    }

    public Cursor[] getCursors() {
        return cursors.toArray(new Cursor[0]);
    }

    public boolean isFreemove() {
        return freemove;
    }

    public void setFreemove(boolean freemove) {
        this.freemove = freemove;
    }

    public List<Entity> getEntities() {
        List<Entity> all = new ArrayList<>();
        all.addAll(players);
        all.addAll(snails);
        all.addAll(boxes);
        all.addAll(cursors);
        return all;
    }

    public boolean hasCursors() {
        return !cursors.isEmpty();
    }

    public void refreshAllCursorTiles() {
        for (Cursor c : cursors) {
            if (c != null) {
                c.refreshSelectableTiles();
            }
        }
    }

    public void cycleCursors() {
        for (Cursor c : cursors) {
            if (c != null) {
                c.cycle();
            }
        }
    }

    public void addHudProperty(Player player, TileType selectedTileType) {
        Tile tileInFront = player.getTileInFront(this);
        if (tileInFront == null) return;

        // Only act if BASIC tile is selected
        if (selectedTileType != TileType.BASIC) return;
        int hudRow = this.getHeight() - 1;
        // Iterate over the properties of the tile in front

        for (int x = 4; x < 11; x++) {
            Tile t = this.getTile(x, hudRow);

            if (t != null && t.getType() == TileType.BLANK) {

                for (Property p : Property.values()) {
                    if (tileInFront.hasEntity()) {
                        if (tileInFront.getEntity().getProperties().contains(p)) {
                            t.setType(p.toTileType());
                            t.clearProperties();
                            t.addProperty(Property.SELECTABLE);
                            break; // first matching property only
                        }
                    }
                    else if (tileInFront.getProperties().contains(p)) {
                        t.setType(p.toTileType());
                        t.clearProperties();
                        t.addProperty(Property.SELECTABLE);
                        break; // first matching property only
                    }
                }

                break; // only add to the first available blank HUD tile
            }
        }

    }
    public Tile getFirstBasicSelectableTile() {
        for (Tile t : getTilesWithProperty(Property.SELECTABLE)) {
            if (t.getType() == TileType.BASIC) return t;
        }
        return null;
    }
    public void moveCursorTo(Tile tile) {
        for (Cursor c : cursors) {
            if (c != null) {
                c.setSelectedTile(tile);  // snaps automatically
            }
        }
    }

    // ---------------- Save Level ----------------
    public void saveToFile(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        LevelData data = new LevelData();

        // Tiles
        data.tiles = new String[height][width];
        data.tileProperties = new List[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = getTile(x, y);
                data.tiles[y][x] = tile.getType().name();
                data.tileClass[y][x] = tile.getClass().getSimpleName();

                data.tileProperties[y][x] = new ArrayList<>();
                for (Property p : tile.getProperties()) {
                    data.tileProperties[y][x].add(p.name());
                }
                if (tile instanceof ArrowTile arrow) {
                    data.tileDir[y][x] = arrow.direction;
                }
            }
        }

        // Entities
        data.entities = new ArrayList<>();
        for (Player p : players) data.entities.add(new EntityData("PLAYER", p.getX(), p.getY()));

        // Boxes
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y);
                if (t.hasEntity() && t.getEntity() instanceof Box) {
                    data.entities.add(new EntityData("BOX", x, y));
                }
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y);
                if (t.hasEntity() && t.getEntity() instanceof Snail s) {
                    data.entities.add(new EntityData("SNAIL", x, y, s.getDirection()));
                }
            }
        }

        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------------- Load Level ----------------
    public static Level loadFromFile(String path) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(path)) {
            LevelData data = gson.fromJson(reader, LevelData.class);
            Level level = new Level();

            // Restore tiles
            for (int y = 0; y < level.height; y++) {
                for (int x = 0; x < level.width; x++) {

                    String tileClass = data.tileClass[y][x];
                    Tile tile;

                    if ("ArrowTile".equals(tileClass)) {

                        Direction savedDir = data.tileDir[y][x];
                        tile = new ArrowTile(x, y, savedDir, level);

                    }
                    else if ("ExitTile".equals(tileClass)) {
                        tile = new ExitTile(x, y, level);
                    }
                    else {

                        tile = new Tile(x, y, level); // <-- create a fresh tile
                    }

                    // Set tile type
                    tile.setType(TileType.valueOf(data.tiles[y][x]));

                    // Set properties
                    if (data.tileProperties[y][x] != null) {
                        for (String prop : data.tileProperties[y][x]) {
                            tile.addProperty(Property.valueOf(prop));
                        }
                    }

                    // Save into level grid
                    level.setTile(x, y, tile);
                }
            }

            // Restore entities
            for (EntityData ed : data.entities) {
                Tile t = level.getTile(ed.x, ed.y);
                switch (ed.type) {
                    case "PLAYER" -> {
                        Player p = new Player(ed.x, ed.y, new AnimationManager(), EntityType.PLAYER, level);
                        level.addPlayer(p);
                    }
                    case "BOX" -> {
                        Box b = new Box(ed.x, ed.y, EntityType.BOX, level);
                        level.addBox(b);
                        level.getTile(ed.x, ed.y).setEntity(b);
                    }
                    case "SNAIL" -> {
                        Snail s = new Snail(ed.x, ed.y, ed.dir, new AnimationManager(), EntityType.SNAIL, level);
                        level.addSnail(s);
                        level.getTile(ed.x, ed.y).setEntity(s);
                    }
                }
            }

            return level;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // ---------------- Helper classes ----------------
    static class LevelData {
        String[][] tiles = new String[9][14];
        String[][] tileClass = new String[9][14];
        Direction[][] tileDir = new Direction[9][14];
        @SuppressWarnings("unchecked")
        List<String>[][] tileProperties = (List<String>[][]) new List[9][14];
        List<EntityData> entities = new ArrayList<>();
    }

    static class EntityData {
        String type;
        int x, y;
        Direction dir; // nullable

        // For Player and Box
        EntityData(String type, int x, int y) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.dir = null;
        }

        // For Snail
        EntityData(String type, int x, int y, Direction dir) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }
}