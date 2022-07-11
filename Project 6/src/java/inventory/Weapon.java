package inventory;

public enum Weapon {
    KitchenKnife(
            "Kitchen Knife",
            "Some japanese kitchen knife your brought to the adventure. Pretty sharp! Be careful with this thing!",
            10
    ),
    Rapier(
            "Rapier",
            "Got lost in some Spanish war, well now it's here.",
            20
    ),
    Sabre(
            "French Sabre",
            "It has dirt on it as if someone dug it underground previously, strange...",
            30
    ),
    MomFlipFlop(
            "Flip-Flops",
            "Dual wielded mommy's flip flops.",
            40
    ),
    GreenKnightAxe(
            "Axe of the Green Knight",
            "The Green Knight's Axe, found with the Knight's head.",
            50
    );

    final String name;
    final String description;
    final int damage;

    Weapon(String name, String description, int damage) {
        this.name = name;
        this.description = description;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDamage() {
        return damage;
    }
}
