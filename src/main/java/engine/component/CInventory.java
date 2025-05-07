package engine.component;

import item.Item;
import item.ItemStack;

import java.util.EnumMap;

public class CInventory extends Component {

    private final int capacity;

    private final EnumMap<Item, Integer> contents;

    public CInventory(final int capacity) {
        this.capacity = capacity;

        contents = new EnumMap<>(Item.class);
    }

    // TODO: добавить метод проверки на наличие места в инвентаре
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
