package player;

import graph.Graph;
import inventory.Inventory;
import inventory.Item;

public class Human extends Player {
    float dmgReception;

    public Human(String iconFilename, String name, int damage, Inventory inv, Graph.Position pos, Graph<Player> graph, float dmgReception) {
        super(iconFilename, name, damage, inv, pos, graph);
        this.dmgReception = dmgReception;
    }

    public void increaseHealth(int amount) {
        health = Math.min(health+amount, maxHealth);
    }

    @Override
    public void decreaseHealth(int damage) {
        health -= inv.getMultiplierEffect(Item.Type.Armor, damage);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Float.floatToIntBits(dmgReception);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Human other = (Human) obj;
        if (Float.floatToIntBits(dmgReception) != Float.floatToIntBits(other.dmgReception))
            return false;
        return true;
    }

    
}
