package inventory;

import java.util.ArrayList;
import java.util.Optional;

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

    // ----GETTERS----

    public Weapon getWeapon() {
        return weapon;
    }

    public Optional<DamageFxItem> getDamageBooster() {
        return damageBooster;
    }

    public Optional<HealthItem> getHealthItem() {
        return healthItem;
    }

    public ArrayList<DefenseItem> getDefenseItems() {
        return new ArrayList<>(defenseItems);
    }

    public void improve(Inventory other) {
        if (other.weapon.ordinal() > weapon.ordinal()) {
            weapon = other.weapon;
        }

        if (other.damageBooster.isPresent()) {
            DamageFxItem otherDmgFx = other.damageBooster.get();

            if (damageBooster.isPresent() &&
                otherDmgFx.ordinal() > damageBooster.get().ordinal()) {
                damageBooster = other.damageBooster;
            } else if (damageBooster.isEmpty()) {
                damageBooster = other.damageBooster;
            }
        }

        if (other.healthItem.isPresent()) {
            HealthItem otherHlth = other.healthItem.get();

            if (healthItem.isPresent() &&
                    otherHlth.ordinal() > healthItem.get().ordinal()) {
                healthItem = other.healthItem;
            } else if (healthItem.isEmpty()) {
                healthItem = other.healthItem;
            }
        }

        for (DefenseItem di : other.defenseItems) {
            if (!defenseItems.contains(di)) {
                defenseItems.add(di);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Weapon: ");
        output.append(weapon.getName());

        output.append(", Damage Booster: ");
        if (damageBooster.isPresent()) {
            output.append(damageBooster.get().getName());
        } else {
            output.append("NONE");
        }

        output.append(", Health Item: ");
        if (healthItem.isPresent()) {
            output.append(healthItem.get().getName());
        } else {
            output.append("NONE");
        }

        return output.toString();
    }
}