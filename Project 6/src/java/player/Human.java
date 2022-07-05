package player;

import inventory.Inventory;

public class Human extends Player {
    // 1.0 receives full damage,
    // 0.0 receives none
    private float dmgReception;

    public Human(String name) {
        // TODO: EDIT FOR HUMAN
        // Icons I like: 🗡⚔
        super("🗡", Type.Human, name, 10, 15, 10, Inventory.HumanDefaultInv());
        dmgReception = 1.0f;
    }

    public float getDmgReception() {
        return dmgReception;
    }
}
