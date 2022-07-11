package game;

import inventory.*;

import java.util.ArrayList;
import java.util.Optional;

import static utilities.AdvRandom.*;

public class GlobalInventory {
    // rare items spawn only once whether as a drop or as a treasure
    // common ones spawn as many times as possible
    // note that all defenseItems are rare and all healthItems are common
    // also that defenseItems only spawn in treasures, Goblins don't drop them

    Weapon[] commonWeapons;
    ArrayList<Weapon> rareWeapons;

    DamageFxItem[] commonDmgFx;
    ArrayList<DamageFxItem> rareDmgFx;

    ArrayList<DefenseItem> defenseItems;

    HealthItem[] healthItems;

    public GlobalInventory() {
        commonWeapons = new Weapon[]{Weapon.Rapier, Weapon.Sabre};
        rareWeapons = new ArrayList<>();
        rareWeapons.add(Weapon.MomFlipFlop);
        rareWeapons.add(Weapon.GreenKnightAxe);

        commonDmgFx = new DamageFxItem[]{DamageFxItem.PeanutButter, DamageFxItem.ProteinShake, DamageFxItem.Taser};
        rareDmgFx = new ArrayList<>();
        rareDmgFx.add(DamageFxItem.Steroids);
        rareDmgFx.add(DamageFxItem.ColdWater);

        defenseItems = new ArrayList<>();
        defenseItems.add(DefenseItem.Helmet);
        defenseItems.add(DefenseItem.Chestplate);
        defenseItems.add(DefenseItem.Leggings);
        defenseItems.add(DefenseItem.Arms);

        healthItems = new HealthItem[]{HealthItem.RedPotion, HealthItem.Gatorade, HealthItem.Spaghetti};
    }

    public Inventory getGoblinOrTreasureInventory(Game.Difficulty difficulty) {
        // Probability of having an item at all (per each slot)
        // For the weapon this means a weapon other than the kitchen knife
        // Easy   -> 10%
        // Normal -> 40%
        // Hard   -> 80%

        // Probability of a rare item (per individual item of the inventory)
        // Easy   -> 5%
        // Normal -> 20%
        // Hard   -> 50%

        Weapon weapon = Weapon.KitchenKnife;
        Optional<DamageFxItem> dmgItem = Optional.empty();
        Optional<HealthItem> healthItem = Optional.empty();
        for (int i = 1; i <= 3; i++) {
            // a.k.a. other than knife for the weapon
            int itemPresentProb = switch (difficulty) {
                case Easy -> 10;
                case Normal -> 40;
                case Hard -> 80;
            };
            boolean itemPresent = nextBoolPercentage(itemPresentProb);

            int rareItemProb = switch (difficulty) {
                case Easy -> 5;
                case Normal -> 20;
                case Hard -> 50;
            };
            boolean rareItem = nextBoolPercentage(rareItemProb);

            switch (i) {
                /*
                General structure of each arm
                if !itemPresent {
                    no item/kitchen knife
                } !rareItem OR we ran out of rares to spawn {
                    common item
                } else it must be rare {
                    rare item
                }
                */

                // Weapon
                case 1 -> {
                    if (!itemPresent) {
                        weapon = Weapon.KitchenKnife;
                    } else if (!rareItem || rareWeapons.size() == 0) {
                        weapon = randomItemArr(commonWeapons);
                    } else {
                        weapon = randomItemList(rareWeapons);
                    }
                }
                // DamageFx
                case 2 -> {
                    if (!itemPresent) {
                        dmgItem = Optional.empty();
                    } else if (!rareItem || rareDmgFx.size() == 0) {
                        dmgItem = Optional.of(randomItemArr(commonDmgFx));
                    } else {
                        dmgItem = Optional.of(randomItemList(rareDmgFx));
                    }
                }
                // Health item
                case 3 -> {
                    // no rare items available, so we just need to know if it's present or not
                    if (!itemPresent) {
                        healthItem = Optional.empty();
                    } else {
                        healthItem = Optional.of(randomItemArr(healthItems));
                    }
                }
            }
        }

        return new Inventory(weapon, dmgItem, healthItem, new ArrayList<>());
    }
}
