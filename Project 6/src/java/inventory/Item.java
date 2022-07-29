package inventory;

class Effect {
    enum Type {
        Scalar, Multiplier
    }
    Type type;

    // Either a scalar (Integer)
    // or a multiplier (Float)
    Object val;

    private Effect(Type type, Object val) {
        this.type = type;
        this.val = val;
    }

    public static Effect scalar(int val) {
        return new Effect(Type.Scalar, Integer.valueOf(val));
    }

    public static Effect multiplier(float val) {
        return new Effect(Type.Multiplier, Float.valueOf(val));
    }
}

public record Item(Type type, Effect effect, String name, String description) {
    public enum Type {
        Weapon,
        HealthItem,
        Armor,
        WeaponEffect
    }

    public int getTotalStat(int initialVal) {
        if (effect.type == Effect.Type.Scalar) {
            return initialVal + (Integer) effect.val;
        } else {
            return (int) (initialVal * (Float) effect.val);
        }
    }

    // ---WEAPONS---
    public class Weapons {
        private static Item weapon(int damage, String name, String description) {
            return new Item(Type.Weapon, Effect.scalar(damage), name, description);
        }

        public static Item kitchenKnife() {
            return weapon(10, "Kitchen Knife", "Knife from your kitchen");
        }

        public static Item sabre() {
            return weapon(20, "Sabre", "Some sabre lost in some war, kind of dirty, looks it was buried for some time");
        }

        public static Item sandal() {
            return weapon(30, "Mommy's Flip-flop",
                    "Flip-flop you picked up from the last time your mother threw it at you and you managed to miss, somehow");
        }
    }

    // ---HEALTH POTIONS---
    public class HealthItems {
        private static Item health(int health, String name, String description) {
            return new Item(Type.HealthItem, Effect.scalar(health), name, description);
        }

        public static Item water() {
            return health(15, "Bottle of Water", "A new, sealed bottle of water");
        }

        public static Item spaghetti() {
            return health(65, "Spaghetti", "A few pounds of spaghetti for added fat protecting you from blows");
        }

        public static Item empanadas() {
            return health(90, "Empanadas", "A few more pounds of empanadas, keep it under control please");
        }
    }

    // ---ARMOR---
    public class Armor {
        private static Item armor(String name, String description) {
            return new Item(Type.Armor, Effect.multiplier(0.5f), name, description);
        }

        public static Item helmet() {
            return armor("Helmet", "Helmet with added leather protection for your head");
        }

        public static Item chestplate() {
            return armor("Chestplate",
                    "Chestplate made out of thick metal, very touch to get through this with a weapon");
        }

        public static Item leggins() {
            return armor("Leggins", "Armored leggins, now walking feels sluggish but nobody will hurt your legs");
        }
    }

    // ---WEAPON FX---
    public class WeaponEffects {
        private static Item weaponFx(float multiplier, String name, String description) {
            return new Item(Type.WeaponEffect, Effect.multiplier(multiplier), name, description);
        }

        public static Item egg() {
            return weaponFx(1.15f, "Fried Egg",
                    "Nothing like putting some fried egg, product of a CPU, on your weapon for extra damage!");
        }

        public static Item taser() {
            return weaponFx(1.5f, "Military-grade Taser",
                    "Electrifying a weapon is such a stupid idea... that's why Dummy Co. manufactured this taser that can electrify weapons for a bit of a shock in company of every hit");
        }

        public static Item nuclearWater() {
            return weaponFx(2.5f, "Nuclear Reactor Water",
                    "Remember the CPU Fried Egg? What if we take it up a level  and use water used in Nuclear Reactor Water Cooling? Another Dummy Co. product, nobody knows how they did it but here it is: water from a nuclear reactor. Guarantee not included, nor available for purchase...");
        }
    }
}
