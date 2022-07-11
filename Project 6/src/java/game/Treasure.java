package game;

import inventory.Inventory;

public class Treasure {
    Inventory loot;
    String icon;

    public Treasure(Inventory loot) {
        this.loot = loot;
        this.icon = "T";
    }

    public String getIcon() {
        return icon;
    }

    public Inventory getInventory() {
        return loot;
    }
}
