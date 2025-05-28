package engine.component.ui;

import engine.component.CInventory;
import engine.object.Sprite;
import engine.util.AssetPool;
import engine.util.listener.MouseListener;
import org.joml.Vector2f;
import org.joml.Vector4f;
import registry.ItemType;
import registry.TextureType;

import java.util.HashMap;
import java.util.Map;

//TODO: сейчас количество строк не ограничено
public class UiInventory extends UiElement {

    // TODO: сделать возможность изменять из настроек
    private CInventory connectedInventory;

    public static final int MAX_COLUMNS = 9;

    public static final float COLUMN_RELATIVE_WIDTH_DOUBLED = 1f / MAX_COLUMNS * 2;

    public static final float DEFAULT_RELATIVE_SCREEN_SIZE = 0.4f;

    private float relativeScreenSize = DEFAULT_RELATIVE_SCREEN_SIZE;

    private final Map<Integer, ItemType> itemBySlot = new HashMap<>();

    private int itemsCount = 0;

    UiSprite bgSprite;

    public UiInventory(final Canvas canvas, final CInventory connectedInventory) {
        super(canvas);

        this.connectedInventory = connectedInventory;

        updateBgSprite();

        uiSprites.add(bgSprite);
    }

    @Override
    protected void update() {
        uiSprites.clear();
        itemBySlot.clear();

        uiSprites.add(bgSprite);
        int counter = 0;
        for (Map.Entry<ItemType, Integer> currItems : connectedInventory.contents.entrySet()) {
            UiSprite uiSprite = new UiSprite(canvas, currItems.getKey().sprite);
            uiSprite.screenPos = getPositionsForItemQuad(counter);
            uiSprite.isClickable = true;
            uiSprites.add(uiSprite);
            itemBySlot.put(counter++, currItems.getKey());
        }

        itemsCount = counter;
    }

    public float getRelativeScreenSize() {
        return relativeScreenSize;
    }

    public UiInventory setRelativeScreenSize(float relativeScreenSize) {
        this.relativeScreenSize = relativeScreenSize;
        updateBgSprite();

        return this;
    }

    private void updateBgSprite() {
        bgSprite = new UiSprite(canvas, new Sprite(AssetPool.getTexture(TextureType.PIXEL)));
        bgSprite.color = new Vector4f(0.5f, 0.5f, 0.6f, 0.7f);
        bgSprite.keepProportions = false;
        bgSprite.screenPos = new Vector4f(-relativeScreenSize, -relativeScreenSize, relativeScreenSize, relativeScreenSize);
    }

    private Vector4f getPositionsForItemQuad(int quadNum) {
        return new Vector4f(
                relativeScreenSize * (quadNum % MAX_COLUMNS * COLUMN_RELATIVE_WIDTH_DOUBLED - 1),
                relativeScreenSize * (1 - (quadNum / MAX_COLUMNS + 1) * COLUMN_RELATIVE_WIDTH_DOUBLED),
                relativeScreenSize * ((quadNum % MAX_COLUMNS + 1) * COLUMN_RELATIVE_WIDTH_DOUBLED - 1),
                relativeScreenSize * (1 - quadNum / MAX_COLUMNS * COLUMN_RELATIVE_WIDTH_DOUBLED)
        );
    }

    public boolean isMouseOverInventory(){
        //TODO: переделать из вычисления вектора прямо в float переменные
        Vector2f pos = new Vector2f(MouseListener.getScreenX(), MouseListener.getScreenY()).sub(new Vector2f((1 - relativeScreenSize) / 2)).div(relativeScreenSize);

        return pos.x < 1 && pos.x > 0 && pos.y < 1 && pos.y > 0;
    }

    public ItemType getItemUnderCursor() {
        //TODO: переделать из вычисления вектора прямо в float переменные
        Vector2f pos = new Vector2f(MouseListener.getScreenX(), MouseListener.getScreenY()).sub(new Vector2f((1 - relativeScreenSize) / 2)).div(relativeScreenSize);

        int column = (int) (pos.x * 2 / COLUMN_RELATIVE_WIDTH_DOUBLED);
        int row = (int) (pos.y * 2 / COLUMN_RELATIVE_WIDTH_DOUBLED);
        int key = row * MAX_COLUMNS + column;

        return itemBySlot.get(key);
    }
}
