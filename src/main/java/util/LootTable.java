package util;

import component.Item;

import java.util.ArrayList;
import java.util.List;

public class LootTable {

    private final List<Item> items;

    private LootTable(List<Item> items) {
        this.items = items;
    }

    public static LootTable of(Item... items) {
        return new LootTable(List.of(items));
    }

    public List<Item> getContent() {
        return new ArrayList<>(items);
    }
}
