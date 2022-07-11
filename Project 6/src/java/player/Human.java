package player;

import inventory.Inventory;

public class Human extends Player {
    private static final int HEAL_RATE = 10;

    // 1.0 receives full damage
    // 0.0 receives none
    // each piece of armour reduces it by 0.25
    private float dmgReception;

    public static final String ICON = "P";

    public Human(String name) {
        // Icons I like: 🗡⚔ Not monospace :((
        super("P", Type.Human, name, Inventory.HumanDefaultInv());
        updateStats();
    }

    @Override
    public void updateStats() {
        super.updateStats();

        dmgReception = 1.0f - inv.getDefenseItems().size()*0.25f;
    }

    public float getDmgReception() {
        return dmgReception;
    }

    public void heal() {
        this.health += HEAL_RATE;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }
}
