package com.quogle.lavarise.sokoban.Level;

import com.quogle.lavarise.client.sokoban.Animations.AnimationManager;
import com.quogle.lavarise.sokoban.*;
import com.quogle.lavarise.sokoban.Entities.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quogle.lavarise.sokoban.Entities.enums.EntityType;
import com.quogle.lavarise.sokoban.Tiles.ArrowTile;
import com.quogle.lavarise.sokoban.Tiles.ButtonTile;
import com.quogle.lavarise.sokoban.Tiles.ExitTile;
import com.quogle.lavarise.sokoban.Tiles.FloorNumTile;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Level {

    private int bpm = 190;
    public int currentFloor = 0;
    public String currentZone = "A";
    private AnimationManager animationManager;

    public boolean freemove = false;
    private int width = 14;
    private int height = 9;
    private Tile[][] tiles;
    private List<Entity> entities = new ArrayList<>();

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

    public List<Tile> getTilesWithProperty(Anomaly property) {
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

    public List<Tile> getTilesWithType(TileType type) {
        List<Tile> result = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y);
                if (t.getType() == type) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    public void ensurePlayerExists() {
        boolean hasPlayer = entities.stream()
                .anyMatch(e -> e.getType() == EntityType.PLAYER);

        if (!hasPlayer) {
            Player p = new Player(0, 0, new AnimationManager(this), EntityType.PLAYER, this);
            addEntity(p);
            getTile(0, 0).setEntity(p);
        }
    }

    public Tile getTileInDirection(Tile tile, Direction dir) {
        if (tile == null || dir == null) return null;

        int x = tile.getX();
        int y = tile.getY();

        return switch (dir) {
            case NONE -> null;
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
    public void addEntity(Entity e) {
        entities.add(e);
    }
    public void setAnimationManager(AnimationManager manager) { this.animationManager = manager; }
    public int getBpm() { return bpm; }
    public void setBpm(int bpm) {
        this.bpm = bpm;
        if (animationManager != null) {
            animationManager.syncAllIdleAnimations();
        }
    }

    public List<Entity> getEntities() {
        List<Entity> all = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y);
                if (t != null) {
                    if (t.getEntity() != null) all.add(t.getEntity());
                    if (t.getMole() != null) all.add(t.getMole()); // add mole here
                }
            }
        }
        return all;
    }

    public void resetCollisionTiles() {
        for (Tile[] row : tiles) {
            for (Tile t : row) {
                t.setState(TileState.NONE);
            }
        }
    }

    public void markCollisionTiles() {
        Map<Tile, Integer> threatCount = new HashMap<>();

        for (Entity e : getEntities()) {
            Direction dir = e.getDirection();
            if (dir == null || dir == Direction.NONE) continue;

            Tile t = getTile(e.getX(), e.getY());
            Tile inFront = getTileInDirection(t, dir);
            if (inFront != null) {
                threatCount.put(inFront, threatCount.getOrDefault(inFront, 0) + 1);
            }
        }

        for (Map.Entry<Tile, Integer> entry : threatCount.entrySet()) {
            if (entry.getValue() >= 2) {
                entry.getKey().setState(TileState.LETHAL);
            }
        }
    }

    public boolean isFreemove() {
        return freemove;
    }

    public void setFreemove(boolean freemove) {
        this.freemove = freemove;
    }



    public boolean hasCursors() {
        return entities.stream().anyMatch(e -> e.getType() == EntityType.CURSOR);
    }

    public void refreshAllCursorTiles() {
        entities.stream()
                .filter(e -> e.getType() == EntityType.CURSOR)
                .map(e -> (Cursor) e)
                .forEach(Cursor::refreshSelectableTiles);
    }

    public void cycleCursors() {
        entities.stream()                 // take the list of entities
                .filter(e -> e.getType() == EntityType.CURSOR)  // only keep cursors
                .map(e -> (Cursor) e)     // cast them to Cursor
                .forEach(Cursor::cycle);  // call cycle() on each
    }

    public void removeEntitiesAt(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile == null) return;

        // Remove normal entity
        Entity e = tile.getEntity();
        if (e != null) {
            tile.setEntity(null);
            entities.remove(e);
        }

        // Remove mole if present
        Entity mole = tile.getMole();
        if (mole != null) {
            tile.setMole(null);
            entities.remove(mole);
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

                for (Anomaly p : Anomaly.values()) {
                    if (tileInFront.hasEntity()) {
                        if (tileInFront.getEntity().getProperties().contains(p)) {
                            t.setType(p.toTileType());
                            t.clearProperties();
                            t.addProperty(Anomaly.SELECTABLE);
                            break; // first matching property only
                        }
                    }
                    else if (tileInFront.getProperties().contains(p)) {
                        t.setType(p.toTileType());
                        t.clearProperties();
                        t.addProperty(Anomaly.SELECTABLE);
                        break; // first matching property only
                    }
                }

                break; // only add to the first available blank HUD tile
            }
        }

    }
    public Tile getFirstBasicSelectableTile() {
        for (Tile t : getTilesWithProperty(Anomaly.SELECTABLE)) {
            if (t.getType() == TileType.BASIC) return t;
        }
        return null;
    }
    public void moveCursorTo(Tile tile) {
        entities.stream()
                .filter(e -> e.getType() == EntityType.CURSOR)
                .map(e -> (Cursor) e)
                .forEach(c -> c.setSelectedTile(tile));
    }

    public void handleVoidEntities() {
        for (Entity e : new ArrayList<>(getEntities())) { // copy to avoid modification issues
            Tile tile = getTile(e.getX(), e.getY());
            if (tile.getType() == TileType.VOID) {
                // Remove from tile
                if (tile.getEntity() == e) tile.setEntity(null);
                if (tile.getMole() == e) tile.setMole(null);
                // Remove from entity list
                entities.remove(e);
                System.out.println(e.getType() + " fell into the void at " + tile.getX() + "," + tile.getY());
            }
        }
    }

    public void handleCollision() {
        for (Entity e : new ArrayList<>(getEntities())) { // copy to avoid modification issues
            Tile tile = getTile(e.getX(), e.getY());
            if (tile.getState() == TileState.LETHAL) {
                // Remove from tile
                if (tile.getEntity() == e) tile.setEntity(null);
                if (tile.getMole() == e) tile.setMole(null);

                if (e.hasProperty(Anomaly.FIRE)) {
                    // Get the tile the entity is on
                    Tile center = this.getTile(e.getX(), e.getY());
                    if (center != null) {
                        // List of offsets for the entity's tile + adjacent tiles (up, down, left, right)
                        int[][] offsets = {
                                {0, 0},    // current tile
                                {0, -1},   // up
                                {0, 1},    // down
                                {-1, 0},   // left
                                {1, 0}     // right
                        };

                        for (int[] offset : offsets) {
                            Tile t = this.getTile(center.getX() + offset[0], center.getY() + offset[1]);
                            if (t != null && t.getType() != TileType.VOID) {
                                t.setType(TileType.CRACKED);
                            }
                        }
                    }
                }

                // Remove from entity list
                entities.remove(e);
            }
        }
    }

    public void handleFireEntities() {
        for (Entity e : new ArrayList<>(getEntities())) { // copy to avoid modification issues
            //Fire does not affect the player
            if (e.getType() == EntityType.PLAYER) continue;

            Tile tile = getTile(e.getX(), e.getY());
            if (tile == null) continue;

            if (tile.hasProperty(Anomaly.FIRE)) {

                // Remove from tile
                if (tile.getEntity() == e) {
                    tile.setEntity(null);
                }

                //Fire damages the tile
                if (tile.getType() == TileType.CRACKED) {
                    tile.setType(TileType.VOID);
                } else {
                    tile.setType(TileType.CRACKED);
                }

                // Remove from entity list
                entities.remove(e);
            }
        }
    }

    public void handleWaterTile() {
        for (Tile t : getTilesWithProperty(Anomaly.WATER)) {
            if (t.hasEntity() && !t.getEntity().getProperties().isEmpty()) {
                Anomaly[] absorbed = (t.getEntity().getProperties().toArray(new Anomaly[0]));
                for (Anomaly anomaly : absorbed) {
                    t.addProperty(anomaly);
                }
                t.getEntity().clearProperties();
                t.removeProperty(Anomaly.WATER);
            }
        }
    }

    public void pressButton() {
        for (Tile t : getTilesWithType(TileType.BUTTON)) {
            if (t instanceof ButtonTile button) {
                button.setPressed(t.hasEntity());
            }
        }
    }

    public void activateExit() {
        boolean allPressed = getTilesWithType(TileType.BUTTON).stream()
                .filter(t -> t instanceof ButtonTile)
                .map(t -> (ButtonTile) t)
                .allMatch(ButtonTile::isPressed);

        for (Tile t : getTilesWithType(TileType.EXIT)) {
            if (t instanceof ExitTile exit) {
                exit.setActive(allPressed);
            }
        }
    }

    public boolean tryEnterExit(Player player) {
        Tile tile = getTile(player.getX(), player.getY());

        if (tile instanceof ExitTile exit && exit.isActive()) {
            advanceFloor();
            return true;
        }

        return false;
    }

    public void advanceFloor() {
        setCurrentFloor(currentFloor + 1);

        String nextFile = "Levels/" + currentZone + "/" + currentFloor + ".json";
        System.out.println("Loading next level: " + nextFile);

        Level next = Level.loadFromFile(nextFile);
        if (next == null) return;

        next.setCurrentZone(currentZone);
        next.setCurrentFloor(currentFloor);

        replaceWith(next);
    }

    private void replaceWith(Level newLevel) {
        this.tiles = newLevel.tiles;
        this.entities = newLevel.entities;
        this.width = newLevel.width;
        this.height = newLevel.height;
    }

    public void enterVoid(Player player) {
        Tile tile = getTile(player.getX(), player.getY());

        if (tile.getType() != TileType.VOID) {
            return;
        }

       resetLevel();
    }

    public void resetLevel() {
        String nextFile = "Levels/" + currentZone + "/" + currentFloor + ".json";
        System.out.println("Reloading level: " + nextFile);

        Level next = Level.loadFromFile(nextFile);
        if (next == null) return;

        next.setCurrentZone(currentZone);
        next.setCurrentFloor(currentFloor);

        replaceWith(next);
    }

    public void setFloorNumbers() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile t = getTile(x, y);
                if (t instanceof FloorNumTile numTile) {
                    numTile.setNumber(currentFloor);
                }
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
                for (Anomaly p : tile.getProperties()) {
                    data.tileProperties[y][x].add(p.name());
                }
                if (tile instanceof ArrowTile arrow) {
                    data.tileDir[y][x] = arrow.direction;
                }
            }
        }

        // Entities
        data.entities = new ArrayList<>();
        for (Entity e : entities) {
            if (e instanceof Player) {
                data.entities.add(new EntityData("PLAYER", e.getX(), e.getY()));
            } else if (e instanceof Box) {
                data.entities.add(new EntityData("BOX", e.getX(), e.getY()));
            } else if (e instanceof Snail s) {
                data.entities.add(new EntityData("SNAIL", e.getX(), e.getY(), s.getDirection()));
            } else if (e instanceof Mole) {
                data.entities.add(new EntityData("MOLE", e.getX(), e.getY()));
            }
            else if (e instanceof Cursor) {
                data.entities.add(new EntityData("CURSOR", e.getX(), e.getY()));
            }

        }

        data.bpm = this.bpm;
        data.currentFloor = this.currentFloor;
        data.currentZone = this.currentZone;

        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(data, writer);
        } catch (IOException ex) {
            ex.printStackTrace();
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
                    else if ("ButtonTile".equals(tileClass)) {
                        tile = new ButtonTile(x, y, level);
                    }
                    else if ("FloorNumTile".equals(tileClass)) {
                        tile = new FloorNumTile(x, y, level);
                    }
                    else {

                        tile = new Tile(x, y, level); // <-- create a fresh tile
                    }

                    // Set tile type
                    tile.setType(TileType.valueOf(data.tiles[y][x]));

                    // Set properties
                    if (data.tileProperties[y][x] != null) {
                        for (String prop : data.tileProperties[y][x]) {
                            tile.addProperty(Anomaly.valueOf(prop));
                        }
                    }

                    // Save into level grid
                    level.setTile(x, y, tile);
                }
            }

            // Restore entities
            for (EntityData ed : data.entities) {
                Tile t = level.getTile(ed.x, ed.y);
                Entity e = switch (ed.type) {
                    case "PLAYER" -> new Player(ed.x, ed.y, new AnimationManager(level), EntityType.PLAYER, level);
                    case "BOX" -> new Box(ed.x, ed.y, EntityType.BOX, level);
                    case "SNAIL" -> new Snail(ed.x, ed.y, ed.dir, new AnimationManager(level), EntityType.SNAIL, level);
                    case "MOLE" -> new Mole(ed.x, ed.y, new AnimationManager(level), EntityType.MOLE, level);
                    case "CURSOR" -> new Cursor(new AnimationManager(level), EntityType.CURSOR, level);
                    default -> null;
                };

                if (e != null) {
                    level.addEntity(e);  // Add to unified list
                    if (t != null) t.setEntity(e); // Set entity reference on the tile
                }
            }
            level.setBpm(data.bpm);
            level.setCurrentFloor(data.currentFloor);
            level.setCurrentZone(data.currentZone);
            level.setFloorNumbers();

            return level;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // ---------------- Helper classes ----------------
    static class LevelData {
        int bpm = 140;
        int currentFloor = 0;
        String currentZone = "A";

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