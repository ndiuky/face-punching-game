package item;

// TODO: нужно как-то обрабатывать добавление нестакабельных предметов
public class ItemStack {

    private final Item item;

    private int stackSize;

    public ItemStack(final Item item) {
        this.item = item;
        stackSize = 1;
    }

    public ItemStack(final Item item, final int initialAmount) {
        this.item = item;
        stackSize = initialAmount;
    }

    public boolean addItem(final Item item) {
        if (item.equals(this.item) && !item.isStackable) {
            stackSize++;

            return true;
        }

        return false;
    }

    /**
     * @return true если удалось добавить предметы в стак, false если нет
     */
    public boolean addItemsFromStack(ItemStack stack) {
        if (item.equals(stack.item) && !item.isStackable) {
            stackSize += stack.stackSize;

            return true;
        }

        return false;
    }

    /**
     * @return количество предметов, которое не удалось удалить из стака
     */
    public int removeItemsFromStack(int amount) {
        if (amount > stackSize) {
            int itemsLeftToRemove = amount - stackSize;
            stackSize = 0;

            return itemsLeftToRemove;
        }

        stackSize -= amount;

        return 0;
    }

    public Item getItem() {
        return item;
    }

    public int getStackSize() {
        return stackSize;
    }

    @Override
    public String toString() {
        return "ItemStack{" + item + "," + stackSize + '}';
    }
}
