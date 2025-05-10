package engine.component.ui;

import engine.component.CInventory;
import engine.object.Sprite;
import engine.util.AssetPool;
import org.joml.Vector4f;
import registry.ItemType;
import registry.TextureType;

import java.util.Map;

public class UiInventory extends UiElement {

    // TODO: сделать возможность изменять из настроек
    private CInventory connectedInventory;

    public static final float DEFAULT_RELATIVE_SCREEN_SIZE = 0.6f;

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

            UiSprite uiSprite = (UiSprite) new UiSprite(canvas, currItems.getKey().sprite).setScreenPos(new Vector4f(-relativeScreenSize, relativeScreenSize - (counter + 1) * 0.2f, -0.8f * relativeScreenSize, relativeScreenSize - counter * 0.2f)).setClickable(true);
            uiSprites.add(uiSprite);

            counter++;
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

    private void updateBgSprite(){
        bgSprite = (UiSprite) new UiSprite(canvas, new Sprite(AssetPool.getTexture(TextureType.PIXEL)))
                .setKeepProportions(false)
                .setScreenPos(new Vector4f(-relativeScreenSize, -relativeScreenSize, relativeScreenSize, relativeScreenSize))
                .setColor(new Vector4f(0.5f, 0.5f, 0.6f, 0.7f));
    }
}
