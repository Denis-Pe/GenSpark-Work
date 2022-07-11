package inventory;

public enum HealthItem {
    RedPotion("Red Potion", "Red potion, looks like there's lighting in the liquid.", 15),
    Gatorade("Gatorade", "Gatorade for a nice refresh.", 40),
    Spaghetti("Spaghetti", "Mom's spaghetti, nice and fresh.", 90);

    final String name;
    final String description;
    final int extraHealth;

    HealthItem(String name, String description, int extraHealth) {
        this.name = name;
        this.description = description;
        this.extraHealth = extraHealth;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getExtraHealth() {
        return extraHealth;
    }
}
