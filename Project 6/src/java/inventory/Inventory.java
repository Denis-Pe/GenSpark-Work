package inventory;

import java.util.ArrayList;

public class Inventory {
	private ArrayList<Item> items;

	public Inventory() {
		items = new ArrayList<>();
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public Inventory withItem(Item item) {
		Inventory inv = new Inventory();
		inv.items.addAll(items);
		inv.items.add(item);
		return inv;
	}

	/**
	 * Replace whatever item has the same Item.Type as the item passed in.
	 * If the item is not there, this won't do anything and will act as
	 * addItem(item).
	 * Note that this will replace *every* item of the same type.
	 * 
	 * @param item
	 */
	public void replaceItemType(Item item) {
		items.removeIf(i -> i.type() == item.type());
		items.add(item);
	}

	/**
	 * Replace whatever item has the same name as the item passed in.
	 * If the item is not there, this won't do anything and will act as
	 * addItem(item).
	 * 
	 * @param item
	 */
	public void replaceItemName(Item item) {
		items.removeIf(i -> i.name().equals(item.name()));
		items.add(item);
	}

	public int getScalarEffect(Item.Type type, int initialValue) {
		return items
				.parallelStream()
				.reduce(0, (acc, next) -> acc + (next.type() == type ? next.getTotalStat(initialValue) : 0),
						(t1, t2) -> t1 + t2);
	}

	public int getMultiplierEffect(Item.Type type, int initialValue) {
		return items
				.parallelStream()
				.reduce(initialValue, (acc, next) -> acc * (int) (next.type() == type ? (Float) next.effect().val : 0),
						(t1, t2) -> t1 + t2);
	}
}
