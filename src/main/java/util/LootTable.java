package util;

import component.ItemDrop;

import java.util.ArrayList;
import java.util.List;

// TODO: попереименовывать, и скорее всего вместо ItemDrop юзать обычные Item
public class LootTable {

    private final List<ItemDrop> itemDrops;

    private LootTable(List<ItemDrop> itemDrops) {
        this.itemDrops = itemDrops;
    }

    public static LootTable of(ItemDrop... itemDrops) {
        return new LootTable(List.of(itemDrops));
    }

    public List<ItemDrop> getContent() {
        return new ArrayList<>(itemDrops);
    }
}
