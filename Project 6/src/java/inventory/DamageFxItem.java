package inventory;

public enum DamageFxItem {
    PeanutButter(
            "Peanut Butter",
            "Nothing like some highly nutritive peanut butter to make you stronger!",
            1.10f
    ),
    ProteinShake(
            "Protein Shake",
            "Generic protein shake",
            1.25f
    ),
    Taser(
            "Taser",
            "Electrify your weapon in order to give your opponent a nice zap when you hit him.",
            1.50f
    ),
    Steroids(
            "Steroids",
            "Nice boost of strength for using your weapon, prolonged use not recommended.",
            1.75f
    ),
    ColdWater(
            "Cold Water",
            "Cold water from the shower.",
            2.00f
    );

    final String name;
    final String description;
    final float multiplier;

    DamageFxItem(String name, String description, float multiplier) {
        this.name = name;
        this.description = description;
        this.multiplier = multiplier;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getMultiplier() {
        return multiplier;
    }
}
