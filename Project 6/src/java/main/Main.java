package main;

import graph.Graph;
import inventory.Inventory;
import inventory.Item;

import static utilities.AdvRandom.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import player.*;

public class Main extends Thread {
    public final static int WIN_WIDTH = 960;
    public final static int WIN_HEIGHT = 768;
    public final static int GRID_WIDTH = 30;
    public final static int GRID_HEIGHT = 24;
    public final static float TILE_LEN = 32.0f;

    public final static String HUMAN_NAME = "human";
    public final static String HUMAN_IMG_FILENAME = "resources/lol-squid.jpg";
    public final static String MAP_NAME = "map";
    public final static String MAP_IMG_FILENAME = "resources/map.png";
    public final static String GOBLIN_IMG_FILENAME = "resources/goblin.png";
    final static String GOBLIN_NAMES_FILENAME = "resources/goblinNames.txt";

    static List<String> goblinNames;

    public static native void add_img(float posX, float posY, float width, float height, String imgFilename,
            String name, boolean isDrawn);

    public static native void move_image(String name, float newX, float newY);

    public static native void clear_imgs();

    public static native boolean is_key_pressed(String key);

    public static native boolean is_key_released(String key);

    public static native boolean is_key_held(String key);

    public static native void run_game(int width, int height);

    static {
        System.loadLibrary("humans_v_goblins_backend");
    }

    enum Difficulty {
        Easy, Normal, Hard
    }

    Graph<Player> graph;
    Difficulty difficulty;

    /**
     * spawn a goblin that is at least {@code distanceFrom} tiles away from
     * {@code pos}
     * 
     * @param distanceFrom
     * @param pos
     * @return Goblin spawned
     */
    Goblin spawnGoblin(int distanceFrom, Graph.Position pos) {
        Inventory inv = new Inventory().withItem(Item.Weapons.kitchenKnife());

        final var name = randomItemList(goblinNames);
        while (graph.anyValueMatches(player -> player.getName().equals(name))) {
            // way to edit it and have it final while also allowing it to be used in the
            // lambda above
            name.replace(name, randomItemList(goblinNames));
        }

        class GoblinPosition {
            public Graph.Position goblinPos = new Graph.Position(randomInt(GRID_WIDTH), randomInt(GRID_HEIGHT));
        }
        final GoblinPosition newGoblinPos = new GoblinPosition();

        // make sure we are spawning distanceFrom tiles away from pos
        while (newGoblinPos.goblinPos.deltaX(pos) < distanceFrom || newGoblinPos.goblinPos.deltaY(pos) < distanceFrom
        // also make sure we are not spawning on other stuff in the map such as other
        // goblins
                || graph.anyEntryMatches(entry -> entry.getKey().equals(newGoblinPos.goblinPos) && entry.getValue() != null)) {
            newGoblinPos.goblinPos = new Graph.Position(randomInt(GRID_WIDTH), randomInt(GRID_HEIGHT));
        }

        var g = new Goblin(GOBLIN_IMG_FILENAME, name, inv.getScalarEffect(Item.Type.Weapon, 0), inv, newGoblinPos.goblinPos, graph);
        return g;
    }

    @Override
    public void run() {
        mainloop();
    }

    // The libraries for windowing and event-handling in Rust
    // do not really like being ran in a thread other than
    // the main thread because of compatibility issues.
    // So here in Java I stay
    // out of their way and everybody is happy
    void mainloop() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("WARNING: Unexpected error: unable to wait during initialization");
        }

        /*
         * Initialize resources:
         * - map
         * - goblin names
         * - graph of players
         * 
         * Event loop:
         * 
         * wait until WASD: move player
         * 
         * switch {
         * next to goblin -> fighting mode,
         * next to lootable (chest, goblin loot) -> pick up loot
         * }
         * 
         * move all goblins towards the player
         * 
         * repeat
         */

        // INITIALIZE MAP
        graph = new Graph<>(GRID_WIDTH, GRID_HEIGHT);
        add_img(0.0f, 0.0f, 1.0f, 1.0f, MAP_IMG_FILENAME, MAP_NAME, true);

        // goblin names
        try {
            goblinNames = Files.readAllLines(Paths.get(GOBLIN_NAMES_FILENAME));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't read goblin names from file");
        }

        // INITIALIZE HUMAN AND GOBLINS
        var humanInv = new Inventory().withItem(Item.Weapons.kitchenKnife());
        var human = new Human(HUMAN_IMG_FILENAME, HUMAN_NAME, humanInv.getScalarEffect(Item.Type.Weapon, 0),
                new Inventory(), new Graph.Position(randomInt(GRID_WIDTH), randomInt(GRID_HEIGHT)), graph, 1.0f);

        var goblins = new ArrayList<Goblin>();

        for (int i = 0; i < 5; i++)
            goblins.add(spawnGoblin(5, human.getPosition()));

        while (true) {
            boolean mov = false;

            if (is_key_released("w")) {
                human.moveNorth(graph);
                mov = true;
            } else if (is_key_released("a")) {
                human.moveWest(graph);
                mov = true;
            } else if (is_key_released("s")) {
                human.moveSouth(graph);
                mov = true;
            } else if (is_key_released("d")) {
                human.moveEast(graph);
                mov = true;
            }

            if (mov) {
                for (Goblin g : goblins) {
                    try {
                        g.moveTo(graph.aStarPath(g.getPosition(), human.getPosition()).get().get(0), graph);
                    } catch (Exception e) {
                        // maybe the goblin is already aside the player?
                        // if it doesn't work for that or whatever reason,
                        // no biggie
                        // UNLESS the graph itself doesn't work, but that's what the unit tests are for
                    }

                }
            }
        }
    }

    public static void main(String[] args) {
        Main javaMainloop = new Main();
        javaMainloop.start();

        run_game(WIN_WIDTH, WIN_HEIGHT);
    }
}