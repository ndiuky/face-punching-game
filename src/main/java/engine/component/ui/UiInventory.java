package engine.component.ui;

import engine.component.CInventory;
import engine.object.Sprite;
import engine.util.AssetPool;
import org.joml.Vector4f;
import registry.ItemType;
import registry.TextureType;

import java.util.Map;

//TODO: сейчас количество строк не ограничено
public class UiInventory extends UiElement {

    // TODO: сделать возможность изменять из настроек
    private CInventory connectedInventory;

    public static final int MAX_COLUMNS = 4;

    public static final float COLUMN_RELATIVE_WIDTH_DOUBLED = 1f / MAX_COLUMNS * 2;

    public static final float DEFAULT_RELATIVE_SCREEN_SIZE = 0.7f;

    private float relativeScreenSize = DEFAULT_RELATIVE_SCREEN_SIZE;

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

        uiSprites.add(bgSprite);
        int counter = 0;
        for (Map.Entry<ItemType, Integer> currItems : connectedInventory.getContents().entrySet()) {
            uiSprites.add((UiSprite) new UiSprite(canvas, currItems.getKey().sprite).setScreenPos(getPositionsForItemQuad(counter++)).setClickable(true));
        }
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
        bgSprite = (UiSprite) new UiSprite(canvas, new Sprite(AssetPool.getTexture(TextureType.PIXEL)))
                .setKeepProportions(false)
                .setScreenPos(new Vector4f(-relativeScreenSize, -relativeScreenSize, relativeScreenSize, relativeScreenSize))
                .setColor(new Vector4f(0.5f, 0.5f, 0.6f, 0.7f));
    }

    private Vector4f getPositionsForItemQuad(int quadNum) {
        return new Vector4f(
                relativeScreenSize * (quadNum % MAX_COLUMNS * COLUMN_RELATIVE_WIDTH_DOUBLED - 1),
                relativeScreenSize * (1 - (quadNum / MAX_COLUMNS + 1) * COLUMN_RELATIVE_WIDTH_DOUBLED),
                relativeScreenSize * ((quadNum % MAX_COLUMNS + 1) * COLUMN_RELATIVE_WIDTH_DOUBLED - 1),
                relativeScreenSize * (1 - quadNum / MAX_COLUMNS * COLUMN_RELATIVE_WIDTH_DOUBLED)
        );
    }
}
