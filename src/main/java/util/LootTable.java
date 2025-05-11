package util;

import component.ItemDrop;

import java.util.ArrayList;
import java.util.List;

public class LootTable {

    public final List<ItemStack> itemStacks;

    private LootTable(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }

    public static LootTable of(ItemStack... itemStacks) {
        return new LootTable(List.of(itemStacks));
    }

    public List<ItemDrop> getContentDropped() {
        List<ItemDrop> result = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            result.add(ItemDrop.generateForItemStack(itemStack));
        }

        return result;
    }
}
