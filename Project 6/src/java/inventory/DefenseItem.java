package inventory;

public enum DefenseItem {
    Helmet("Helmet"),
    Chestplate("Chest plate"),
    Leggings("Leggings"),
    Arms("Arm Protection");

    final String name;

    DefenseItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
