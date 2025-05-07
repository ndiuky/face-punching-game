package engine.component;

import item.Item;
import item.ItemStack;

import java.util.EnumMap;

public class CInventory extends Component {

    private final EnumMap<Item, Integer> contents = new EnumMap<>(Item.class);

    public void addItemStack(final ItemStack pickedStack) {
        contents.merge(pickedStack.getItem(), pickedStack.getStackSize(), Integer::sum);
    }

    /**
     * @return true если удалось убрать нужные предметы из инвентаря, т.е. было найдено
     * нужное количество нужных предметов.
     * false если предметов оказалось меньше чем необходимо, предметы отниматься
     * не будут.
     */
    public boolean removeItems(Item item, int amount) {
        if (amountOfItems(item) < amount) {
            return false;
        }

        contents.merge(item, amount, (a, b) -> {
            final int result = a - b;
            if (result <= 0) {
                contents.remove(item);

                return null;
            }

            return result;
        });

        return true;
    }

    public int amountOfItems(Item item) {
        return contents.getOrDefault(item, 0);
    }

    public EnumMap<Item, Integer> getContents() {
        return new EnumMap<>(contents);
    }
}
