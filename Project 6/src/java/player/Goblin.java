package player;

import graph.Graph;
import inventory.Inventory;

public class Goblin extends Player {
    public Goblin(String iconFilename, String name, int damage, Inventory inv, Graph.Position pos, Graph<Player> graph) {
        super(iconFilename, name, damage, inv, pos, graph);
    }
}
