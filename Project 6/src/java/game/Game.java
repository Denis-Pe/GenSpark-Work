package game;

import player.Goblin;
import player.Human;
import player.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static utilities.AdvRandom.*;
import utilities.Input;

@SuppressWarnings("BusyWait")
public class Game {
    // each this number of turns,
    // a goblin spawns
    private final static int GEN_GOBLIN_TURNS = 5;
    private final static int INC_DIFF_TURNS = 20;
    private final static int MAP_X = 20;
    private final static int MAP_Y = 10;
    private final static int DIST_FROM_PLAYER = 5;

    // contains all the information, including the player & goblins
    private Map map;

    GlobalInventory gInventory;
    private int turn;

    enum Difficulty {
        Easy, Normal, Hard
    };
    private Difficulty difficulty;

    public Game(String humanName) {
        map = new Map(MAP_X, MAP_Y);

        difficulty = Difficulty.Easy;
        turn = 0;
        gInventory = new GlobalInventory();

        Human h = new Human(humanName);
        map.setAt(4, 4, h);
    }

    public void nextTurn(Input input) {
        int[] humanPos = getHumanPos();
        Human h = (Human) map.getAt(humanPos[0], humanPos[1]).getState();
        ArrayList<Character> possibleDirections =
                freeSlotsSurrounding(humanPos[0], humanPos[1]);

        System.out.println("Inventory: " + h.getInventory());

        System.out.println("Choose an action. Options:");
        for (char a : possibleDirections) {
            System.out.print(switch (a) {
                case 'n' -> "North ";
                case 's' -> "South ";
                case 'w' -> "West ";
                case 'e' -> "East ";
                default -> throw new IllegalStateException("Unexpected value: " + a);
            });
        }
        System.out.println("Pass");

        char move = input.nextLineNonempty().toLowerCase().charAt(0);
        while (!possibleDirections.contains(move) && move != 'p') {
            System.out.println("That is not an option. Try again:\n");
            move = input.nextLineNonempty().toLowerCase().charAt(0);
        }

        map.setAt(humanPos[0], humanPos[1], null);
        switch (move) {
            case 'n' -> humanPos[1]--;
            case 's' -> humanPos[1]++;
            case 'w' -> humanPos[0]--;
            case 'e' -> humanPos[0]++;
        } // if it's p nothing happens

        h.heal();

        map.setAt(humanPos[0], humanPos[1], h);

        getTreasureIfApplicable(humanPos[0], humanPos[1], h, input);
        combatIfApplicable(humanPos[0], humanPos[1], input);

        if (turn % GEN_GOBLIN_TURNS == 0) {
            spawnGoblin(DIST_FROM_PLAYER);
        }
        if (turn % INC_DIFF_TURNS == 0 && turn >= INC_DIFF_TURNS) {
            switch (difficulty) {
                case Easy -> difficulty = Difficulty.Normal;
                case Normal -> difficulty = Difficulty.Hard;
            }
        }

        turn++;
    }

    public void printMap() {
        System.out.println(map);
    }

    /// the free slots surrounding this slot
    /// returns an ArrayList containing characters
    /// representing the 4 directions
    /// like so: ['w', 's', 'e']
    private ArrayList<Character> freeSlotsSurrounding(int x, int y) {
        ArrayList<Character> output = new ArrayList<>();

        // if it fails because it's out of bounds,
        // no problem it just means it isn't available
        try {
            if (map.getAt(x, y-1).isEmpty()) {
                output.add('n');
            }
        } catch (Exception ignored) {}

        try {
            if (map.getAt(x, y+1).isEmpty()) {
                output.add('s');
            }
        } catch (Exception ignored) {}

        try {
            if (map.getAt(x-1, y).isEmpty()) {
                output.add('w');
            }
        } catch (Exception ignored) {}

        try {
            if (map.getAt(x+1, y).isEmpty()) {
                output.add('e');
            }
        } catch (Exception ignored) {}

        return output;
    }

    private int[] getHumanPos() {
        int[] index = new int[2];

        Outer:
        for (int y = 0; y < MAP_Y; y++) {
            for (int x = 0; x < MAP_X; x++) {
                if (map.getAt(x, y).getIcon().equals(Human.ICON)) {
                    index[0] = x;
                    index[1] = y;
                    break Outer;
                }
            }
        }

        return index;
    }

    private void combatIfApplicable(int humanPosX, int humanPosY, Input input) {
        boolean applicable = false;
        Goblin goblin = null;
        int goblinPosX = 0;
        int goblinPosY = 0;

        Outer:
        for (int y = 0; y < MAP_Y; y++) {
            for (int x = 0; x < MAP_X; x++) {
                if (map.getAt(x, y).getState() instanceof Goblin) {
                    if (Math.abs(humanPosX - x) == 1 && humanPosY - y == 0
                        || Math.abs(humanPosY - y) == 1 && humanPosX - x == 0) {
                        applicable = true;
                        goblin = (Goblin) map.getAt(x, y).getState();
                        goblinPosX = x;
                        goblinPosY = y;
                        break Outer;
                    }
                }
            }
        }

        if (!applicable) return;

        System.out.printf("Combat! You are facing %s the %s. Let the fight start!\n", goblin.getName(), goblin.getTypeName());

        Human human = (Human) map.getAt(humanPosX, humanPosY).getState();

        while (!human.isDead() && !goblin.isDead()) {
            System.out.println(Player.attack(human, goblin));

            try {
                Thread.sleep(3500);
            } catch (Exception ignored) {}

            if (goblin.isDead()) break;

            System.out.println(Player.attack(goblin, human));

            try {
                Thread.sleep(3500);
            } catch (Exception ignored) {}

            System.out.println(human.getHealth());
            System.out.println(goblin.getHealth());
        }

        if (human.isDead()) {
            System.out.printf("You've been slain. %s's inventory is as follows: %s\n", goblin.getName(), goblin.getInventory().toString());
            System.exit(0);
        } else if (goblin.isDead()) {
            System.out.println("This goblin has been slain.");
        }

        pickUpLoot(human, goblin, input);
        map.setAt(goblinPosX, goblinPosY, null);
        spawnTreasure(DIST_FROM_PLAYER);
    }

    private void pickUpLoot(Human human, Goblin goblin, Input input) {
        System.out.println("Do you want to pick up any better items that this Goblin dropped? (yes or no)");
        char ans = input.nextLineNonempty().toLowerCase().charAt(0);
        while (ans != 'n' && ans != 'y') {
            System.out.println("That is not a valid input.");
            ans = input.nextLineNonempty().toLowerCase().charAt(0);
        }

        if (ans == 'y') {
            human.improveInv(goblin.dropLoot());
            human.updateStats();
        }
    }

    private void getTreasureIfApplicable(int humanPosX, int humanPosY, Human human, Input input) {
        boolean applicable = false;
        Treasure treasure = null;
        int treasurePosX = 0;
        int treasurePosY = 0;

        Outer:
        for (int y = 0; y < MAP_Y; y++) {
            for (int x = 0; x < MAP_X; x++) {
                if (map.getAt(x, y).getState() instanceof Treasure) {
                    if (Math.abs(humanPosX - x) == 1 && humanPosY - y == 0
                            || Math.abs(humanPosY - y) == 1 && humanPosX - x == 0) {
                        applicable = true;
                        treasure = (Treasure) map.getAt(x, y).getState();
                        treasurePosX = x;
                        treasurePosY = y;
                        break Outer;
                    }
                }
            }
        }

        if (!applicable) return;

        System.out.println("A treasure!! Do you want to pick up any better items in here? (yes or no)");
        char ans = input.nextLineNonempty().toLowerCase().charAt(0);
        while (ans != 'n' && ans != 'y') {
            System.out.println("That is not a valid input.");
            ans = input.nextLineNonempty().toLowerCase().charAt(0);
        }

        if (ans == 'y') {
            human.improveInv(treasure);
            human.updateStats();
        }

        map.setAt(treasurePosX, treasurePosY, null);
    }

    // Name chosen at random and ensuring that it's not an existing name
    // But if I can't read the file for any reason, a very random string is returned
    private String getNewGoblinName() {
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/goblinNames.txt"))) {
            List<String> names = reader.lines().toList();

            // to ensure unique names:
            String name = randomItemList(names);
            List<String> goblinNames = map.getGoblinNames();

            while (goblinNames.contains(name)) {
                name = randomItemList(names);
            }

            return randomItemList(names);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e);
            return randomStr(6);
        }
    }

    private void spawnGoblin(int minDistFromPlayer) {
        ArrayList<int[]> possibleIndexes = new ArrayList<>();
        int[] humanPos = getHumanPos();

        for (int y = 0; y < MAP_Y; y++) {
            for (int x = 0; x < MAP_X; x++) {
                int[] ind = new int[]{x, y};
                if (map.getAt(x, y).isEmpty()) {
                    possibleIndexes.add(ind);
                }
            }
        }

        int[] index = randomItemList(possibleIndexes);
        while (Math.abs(index[0] - humanPos[0]) < minDistFromPlayer
            || Math.abs(index[1] - humanPos[1]) < minDistFromPlayer) {
            index = randomItemList(possibleIndexes);
        }

        Goblin g = new Goblin(getNewGoblinName(), gInventory.getGoblinOrTreasureInventory(difficulty));

        map.setAt(index[0], index[1], g);
    }

    private void spawnTreasure(int minDistFromPlayer) {
        ArrayList<int[]> possibleIndexes = new ArrayList<>();
        int[] humanPos = getHumanPos();

        for (int y = 0; y < MAP_Y; y++) {
            for (int x = 0; x < MAP_X; x++) {
                int[] ind = new int[]{x, y};
                if (map.getAt(x, y).isEmpty()) {
                    possibleIndexes.add(ind);
                }
            }
        }

        int[] index = randomItemList(possibleIndexes);
        while (Math.abs(index[0] - humanPos[0]) < minDistFromPlayer
                || Math.abs(index[1] - humanPos[1]) < minDistFromPlayer) {
            index = randomItemList(possibleIndexes);
        }

        Treasure t = new Treasure(gInventory.getGoblinOrTreasureInventory(difficulty));
        map.setAt(index[0], index[1], t);
    }
}
