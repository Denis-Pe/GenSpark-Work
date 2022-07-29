package player;

import graph.Graph;
import inventory.Inventory;
import main.Main;

public abstract class Player {
    final static int DEFAULT_HEALTH = 10;
    final static float CRITICAL_MULTIPLIER = 1.5f;

    private String iconFilename;
    private String name;
    protected int health;
    protected int maxHealth;
    private int damage;
    protected Inventory inv;
    private Graph.Position pos;

    Player(String iconFilename, String name, int damage, Inventory inv, Graph.Position pos, Graph<Player> graph) {
        this.iconFilename = iconFilename;
        this.name = name;
        this.health = DEFAULT_HEALTH;
        this.maxHealth = DEFAULT_HEALTH;
        this.damage = damage;
        this.inv = inv;
        this.pos = pos;

        graph.setAt(pos, this);
        draw();
    }

    public void moveTo(Graph.Position newPos, Graph<Player> graph) {
        if (!graph.getAt(pos).equals(this)) {
            throw new IllegalAccessError(
                    "Graph does not contain player at the position equal to the player's position");
        }

        graph.movePosToPos(pos, newPos);
        pos = newPos;
        Main.move_image(
                name,
                pos.x() * Main.TILE_LEN / Main.WIN_WIDTH,
                pos.y() * Main.TILE_LEN / Main.WIN_HEIGHT);
    }

    public void moveNorth(Graph<Player> graph) {
        if (pos.y() - 1 >= 0) {
            moveTo(pos.decY(), graph);
        }
    }

    public void moveWest(Graph<Player> graph) {
        if (pos.x() - 1 >= 0) {
            moveTo(pos.decX(), graph);
        }
    }

    public void moveSouth(Graph<Player> graph) {
        if (pos.y() + 1 >= 0) {
            moveTo(pos.incY(), graph);
        }
    }
    
    public void moveEast(Graph<Player> graph) {
        if (pos.x() + 1 >= 0) {
            moveTo(pos.incX(), graph);
        }
    }

    public void draw() {
        Main.add_img(
                pos.x() * Main.TILE_LEN / Main.WIN_WIDTH,
                pos.y() * Main.TILE_LEN / Main.WIN_HEIGHT,
                Main.TILE_LEN / Main.WIN_WIDTH,
                Main.TILE_LEN / Main.WIN_HEIGHT,
                iconFilename,
                name,
                true);
    }

    // this can be overriden so that it does
    // other stuff like reduced damage or
    // increased damage
    // i.e. in Human
    public void decreaseHealth(int damage) {
        health -= damage;
    }

    public void attack(Player other, boolean critical) {
        int damage = critical ? Math.round(this.damage * CRITICAL_MULTIPLIER) : this.damage;

        other.decreaseHealth(damage);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + damage;
        result = prime * result + health;
        result = prime * result + ((iconFilename == null) ? 0 : iconFilename.hashCode());
        result = prime * result + ((inv == null) ? 0 : inv.hashCode());
        result = prime * result + maxHealth;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (damage != other.damage)
            return false;
        if (health != other.health)
            return false;
        if (iconFilename == null) {
            if (other.iconFilename != null)
                return false;
        } else if (!iconFilename.equals(other.iconFilename))
            return false;
        if (inv == null) {
            if (other.inv != null)
                return false;
        } else if (!inv.equals(other.inv))
            return false;
        if (maxHealth != other.maxHealth)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (pos == null) {
            if (other.pos != null)
                return false;
        } else if (!pos.equals(other.pos))
            return false;
        return true;
    }

    public String getIconFilename() {
        return iconFilename;
    }

    public String getName() {
        return name;
    }

    public Graph.Position getPosition() {
        return pos;
    }
}
