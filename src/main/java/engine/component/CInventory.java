package engine.component;

import registry.ItemType;
import util.ItemStack;

import java.util.EnumMap;

public class CInventory extends Component {

    public final EnumMap<ItemType, Integer> contents = new EnumMap<>(ItemType.class);

    public void addItemStack(final ItemStack pickedStack) {
        contents.merge(pickedStack.item, pickedStack.stackSize, Integer::sum);
    }

    /**
     * @return true если удалось убрать нужные предметы из инвентаря, т.е. было найдено
     * нужное количество нужных предметов.
     * false если предметов оказалось меньше чем необходимо, предметы отниматься
     * не будут.
     */
    public boolean removeItems(ItemType item, int amount) {
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

    public int amountOfItems(ItemType item) {
        return contents.getOrDefault(item, 0);
    }
}
