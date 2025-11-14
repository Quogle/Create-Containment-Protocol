package com.quogle.lavarise.sokoban.Level;

import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.Box;
import com.quogle.lavarise.sokoban.Entities.Cursor;
import com.quogle.lavarise.sokoban.Entities.Player;
import com.quogle.lavarise.sokoban.Entities.Snail;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Level {
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
            Player p = new Player(0, 0, new AnimationManager(), EntityType.PLAYER);
            addPlayer(p);
        }
    }

    public Tile getTileInDirection(Tile tile, Direction dir) {
        int x = tile.getX();
        int y = tile.getY();

        return switch (dir) {
            case UP    -> getTile(x, y - 1);
            case DOWN  -> getTile(x, y + 1);
            case LEFT  -> getTile(x - 1, y);
            case RIGHT -> getTile(x + 1, y);
        };
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Tile[][] getTiles() { return tiles; }
    public List<Player> getPlayers() { return players; }
    public void addPlayer(Player p) { players.add(p); }
    public void addSnail(Snail s) { snails.add(s); }
    public Snail[] getSnails() {
        return snails.toArray(new Snail[0]);
    }
    public void addBox(Box b) { boxes.add(b); }
    public Box[] getBoxes() {return boxes.toArray(new Box[0]);}
    public void addCursor(Cursor c) { cursors.add(c); }
    public Cursor[] getCursors() {return cursors.toArray(new Cursor[0]);}
    public boolean isFreemove() {return freemove;}
    public void setFreemove(boolean freemove) {this.freemove = freemove;}

    public List<Entity> getAllEntities() {
        List<Entity> all = new ArrayList<>();
        all.addAll(players);
        all.addAll(snails);
        all.addAll(boxes);
        return all;
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
                Tile t = getTile(x, y);
                data.tiles[y][x] = t.getType().name();

                data.tileProperties[y][x] = new ArrayList<>();
                for (Property p : t.getProperties()) {
                    data.tileProperties[y][x].add(p.name());
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
                    level.getTile(x, y).setType(TileType.valueOf(data.tiles[y][x]));
                    if (data.tileProperties[y][x] != null) {
                        for (String prop : data.tileProperties[y][x]) {
                            level.getTile(x, y).addProperty(Property.valueOf(prop));
                        }
                    }
                }
            }

            // Restore entities
            for (EntityData ed : data.entities) {
                Tile t = level.getTile(ed.x, ed.y);
                switch (ed.type) {
                    case "PLAYER" -> {
                        Player p = new Player(ed.x, ed.y, new AnimationManager(), EntityType.PLAYER);
                        level.addPlayer(p);
                    }
                    case "BOX" -> {
                    Box b = new Box(ed.x, ed.y, EntityType.BOX);
                        level.addBox(b);
                    }
                    case "SNAIL" -> {
                        Snail s = new Snail(ed.x, ed.y,  ed.dir, new AnimationManager(), EntityType.SNAIL);
                        level.addSnail(s);
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
        @SuppressWarnings("unchecked")
        List<String>[][] tileProperties = (List<String>[][]) new List[9][14];
        List<EntityData> entities = new ArrayList<>();
    }

    static class EntityData {
        String type;
        int x, y;
        public Direction dir;
        EntityData(String type, int x, int y) { this.type = type; this.x = x; this.y = y; }
    }
}
