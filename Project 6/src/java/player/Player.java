package player;

import java.io.BufferedReader;
import java.io.FileReader;
import inventory.Inventory;

public class Player {
    static String[] UNLIKELY_CRITICAL_HIT_MSGS;
    static String LIKELY_CRITICAL_HIT_MSG = "%s %s hits %s %s with a critical hit for %d damage.";
    static String NO_CRITICAL = "%s %s hits %s %s and inflicts %d damage.";

    final static String PLAYER_DEATH = "You've been slain by %s the %s.";
    final static String GOBLIN_DEATH = "You have slain %s the %s";

    private final String icon;

    public enum Type {
        Goblin, Human
    }
    private final Type type;

    private final String name;

    private int health;
    private int maxHealth;

    // base because this is by default,
    // the inventory will add or subtract but this
    // will not take it into account
    private int baseStrength;

    // goblins drop this
    private Inventory inv;

    public Player(String icon, Type type, String name, int health, int maxHealth, int strength, Inventory inv) {
        this.icon = icon;
        this.name = name;
        this.type = type;
        this.health = health;
        this.maxHealth = maxHealth;
        this.baseStrength = strength;
        this.inv = inv;

        try (BufferedReader reader = new BufferedReader(new FileReader("resources/unlikelyCriticalHitMsgs.txt"))) {
            UNLIKELY_CRITICAL_HIT_MSGS = (String[]) reader.lines().toList().toArray();
        } catch (Exception e) {
            UNLIKELY_CRITICAL_HIT_MSGS = new String[]{"No way! %s %s has struck %s %s with a critical hit of %d damage!"};
        }
    }

    /// Return the message of the hit
    public static String attack(Player attacker, Player attacked) {
        // ==Calculate whether this is a critical hit==
        //         BASE STRENGTH | MAX  STRENGTH
        // player.Player |     10       |     100
        // Goblin |   varies^1   |     100
        // ^1: random but ensuring it starts off easy and gets harder as the game goes on
        //
        // Critical likelihood is based on a random number between 1 and 100 inclusive
        // subtract the attacker's strength, with a critical hit when the result thereof
        // is less than or equal to zero. So, we should in theory get attacker.strength%
        // chance of a critical hit. With "unlikely" being 50% chance or below and "likely"
        // being above that
        //
        // Now what does a critical hit mean? The attacker hits 1.5 times their normal strength
        // and of course the critical hit message
        boolean critical = (Math.random() * 100 + 1) - attacker.getStrength() <= 0;

        int damage = critical? (int) Math.round(attacker.getStrength()*1.5) : attacker.getStrength();
        if (attacked instanceof Human h) {
            damage *= (int) h.getDmgReception();
        }

        attacked.health -= damage;

        // if critical, return a member of UNLIKELY... at random, else return LIKELY...
        return critical? UNLIKELY_CRITICAL_HIT_MSGS[(int)(Math.random() * UNLIKELY_CRITICAL_HIT_MSGS.length)]
            : LIKELY_CRITICAL_HIT_MSG;
    }

    // ----GETTERS----

    public String getTypeName() {
        return type == Type.Goblin ? "Goblin" : "player.Human";
    }

    // TODO: FACTOR IN INVENTORY FOR A PLAYER
    public int getStrength() {
        return baseStrength;
    }
}