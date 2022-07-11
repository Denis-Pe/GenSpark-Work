package player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import game.Treasure;
import inventory.DamageFxItem;
import inventory.HealthItem;
import inventory.Inventory;
import inventory.Weapon;

import static utilities.AdvRandom.*;

public class Player {
    static String[] unlikelyCriticalHitMsgs;
    static String likelyCriticalHitMsg = "%s %s hits %s %s with a critical hit for %d damage.";
    static String noCriticalMsg = "%s %s hits %s %s and inflicts %d damage.";

    final static String PLAYER_DEATH = "You've been slain by %s the %s.";
    final static String GOBLIN_DEATH = "You have slain %s the %s";

    final static int DEFAULT_HEALTH = 10;

    private String icon;

    public int getHealth() {
        return health;
    }

    public enum Type {
        Goblin, Human
    }
    private Type type;

    private String name;

    int health;
    int maxHealth;

    private int damage;

    // goblins drop this
    protected Inventory inv;

    public Player(String icon, Type type, String name, Inventory inv) {
        this.icon = icon;
        this.name = name;
        this.type = type;
        this.health = DEFAULT_HEALTH;
        this.maxHealth = DEFAULT_HEALTH;
        this.inv = inv;

        if (type == Type.Human) {
            maxHealth += 10;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/unlikelyCriticalHitMsgs.txt"))) {
            List<String> temp = reader.lines().toList();

            unlikelyCriticalHitMsgs = new String[temp.size()];
            for (int i = 0; i < temp.size(); i++) {
                unlikelyCriticalHitMsgs[i] = temp.get(i);
            }
        } catch (Exception e) {
            unlikelyCriticalHitMsgs = new String[]{"No way! %s %s has struck %s %s with a critical hit of %d damage!"};
        }

        updateStats();
    }

    public void updateStats() {
        Weapon w = inv.getWeapon();
        damage = w.getDamage();

        float dmgMultiplier = inv.getDamageBooster().map(DamageFxItem::getMultiplier).orElse(1.0f);
        damage = Math.round(damage*dmgMultiplier);

        int extraHealth = inv.getHealthItem().map(HealthItem::getExtraHealth).orElse(0);
        maxHealth = DEFAULT_HEALTH + extraHealth;

        if (type == Type.Human) {
            maxHealth += 10;
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    public void improveInv(Treasure loot) {
        inv.improve(loot.getInventory());
    }

    /// Return the message of the hit
    public static String attack(Player attacker, Player attacked) {
        // ==Calculate whether this is a critical hit==
        // Critical likelihood is attacker.damage percent
        // chance of a critical hit. With "unlikely" being 50% chance or below and "likely"
        // being above that
        //
        // Now what does a critical hit mean? The attacker hits 1.5 times their normal damage
        // and of course the critical hit message
        boolean critical = nextBoolPercentage(attacker.damage);

        int damage = critical? Math.round(attacker.getDamage()*1.5f) : attacker.damage;
        if (attacked instanceof Human h) {
            damage = (int) (damage*h.getDmgReception());
        }

        attacked.health -= damage;

        if (critical) {
            return (attacker.damage <= 50 ? randomItemArr(unlikelyCriticalHitMsgs) : likelyCriticalHitMsg)
                    .formatted(attacker.name, attacker.getTypeName(), attacked.name, attacked.getTypeName(), damage);
        } else {
            return noCriticalMsg.formatted(attacker.name, attacker.getTypeName(), attacked.name, attacked.getTypeName(), damage);
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    // ----GETTERS----

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return type == Type.Goblin ? "Goblin" : "Human";
    }

    public int getDamage() {
        return damage;
    }
}