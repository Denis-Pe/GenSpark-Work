package inventory;

import java.util.ArrayList;
import java.util.Optional;

enum Weapon {
    KitchenKnife, Rapier, Sabre, MomFlipFlop, GreenKnightAxe;

    String name;
    String description;
    int damage;

    Weapon() {
        switch (this) {
            case KitchenKnife -> {
                name = "Kitchen Knife";
                description = "Some japanese kitchen knife your brought to the adventure. Pretty sharp! Be careful with this thing!";
                damage = 10;
            }
            case Rapier -> {
                name = "Rapier";
                description = "Got lost in some Spanish war, well now it's here.";
                damage = 20;
            }
            case Sabre -> {
                name ="French Sabre";
                description = "It has dirt on it as if someone dug it underground previously, strange...";
                damage = 30;
            }
            case MomFlipFlop -> {
                name = "Flip-Flops";
                description = "Dual wielded mommy's flip flops.";
                damage = 40;
            }
            case GreenKnightAxe -> {
                name = "Axe of the Green Knight";
                description = "The Green Knight's Axe, found with the Knight's head.";
                damage = 50;
            }
        };
    }
}

enum DamageFxItem {
    PeanutButter, ProteinShake, Taser, Steroids, ColdWater;

    String name;
    String description;
    float multiplier;

    DamageFxItem() {
        switch (this) {
            case PeanutButter -> {
                name = "Peanut Butter";
                description = "Nothing like some highly nutritive peanut butter to make you stronger!";
                multiplier = 1.10f;
            }
            case ProteinShake -> {
                name = "Protein Shake";
                description = "Bought at Walmart.";
                multiplier = 1.25f;
            }
            case Taser -> {
                name = "Taser";
                // whether it is realistic, I don't know lol
                description = "Electrify your weapon in order to give your opponent a nice zap when you hit him.";
                multiplier = 1.50f;
            }
            case Steroids -> {
                name = "Steroids";
                description = "Nice boost of strength for using your weapon, prolonged use not recommended.";
                multiplier = 1.75f;
            }
            case ColdWater -> {
                name = "Cold Water";
                description = "Cold water from the shower.";
                multiplier = 2.00f;
            }
        };
    }
}

enum DefenseItem {
    Helmet, Chestplate, Leggings, Arms;

    String name;
    String description;

    DefenseItem() {
        switch (this) {
            case Helmet -> {
                name = "Helmet";
                description = "Wear protection for you skin and hair under this metal helmet.";
            }
            case Chestplate -> {
                name = "Chest-plate";
                description = "This baby will protect you very nicely.";
            }
            case Leggings -> {
                name = "Leggings";
                description = "Now you can receive blows to the legs and without worries.";
            }
            case Arms -> {
                name = "Arm Protection";
                description = "Very well made piece of armor covering your arms, increased mobility.";
            }
        };
    }
}

enum HealthItem {
    RedPotion, Gatorade, Spaghetti;

    String name;
    String description;
    int extraHealth;

    HealthItem() {
        switch (this) {
            case RedPotion -> {
                name = "Red Potion";
                description = "Red potion, looks like there's lighting in the liquid.";
                extraHealth = 15;
            }
            case Gatorade -> {
                name = "Gatorade";
                description = "Gatorade for a nice refresh.";
                extraHealth = 40;
            }
            case Spaghetti -> {
                name = "Spaghetti";
                description = "Mom's spaghetti, found in the lunchbox you lost some time ago. Still nice and fresh.";
                extraHealth = 90;
            }
        };
    }
}

public class Inventory {
    Weapon weapon;
    Optional<DamageFxItem> damageBooster;
    Optional<HealthItem> healthItem;
    ArrayList<DefenseItem> defenseItems;

    public static Inventory HumanDefaultInv() {
        return new Inventory(Weapon.KitchenKnife, Optional.empty(), Optional.empty(), new ArrayList<>());
    }

    public Inventory(Weapon weapon,
                     Optional<DamageFxItem> damageBooster,
                     Optional<HealthItem> healthItem,
                     ArrayList<DefenseItem> defenseItems) {
        this.weapon = weapon;
        this.damageBooster = damageBooster;
        this.healthItem = healthItem;
        this.defenseItems = defenseItems;
    }
}