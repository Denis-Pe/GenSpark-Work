package player;

import game.Treasure;
import inventory.Inventory;

public class Goblin extends Player {
    public Goblin(String name, Inventory inv) {
        // goblin face: \uD83D\uDC7A not monospace with the font IntelliJ uses :((((
        // black box: ■
        super("G", Type.Goblin, name, inv);

    }

    @Override
    public Goblin clone() {
        return new Goblin(getName(), inv);
    }

    public Treasure dropLoot() {
        return new Treasure(inv);
    }
}
