package engine.component.ui;

import engine.component.CInventory;
import engine.util.AssetPool;
import item.Item;
import org.joml.Vector4f;

import java.util.EnumMap;
import java.util.Map;

public class UiTotalItems extends UiElement {

    private final CInventory connectedInventory;

    private EnumMap<Item, Integer> contents = new EnumMap<>(Item.class);

    private boolean isDirty = true;

    public UiTotalItems(Canvas canvas, CInventory connectedInventory) {
        super(canvas);

        this.connectedInventory = connectedInventory;

        uiSprites.add((UiSprite) new UiSprite(canvas, Item.NUT.sprite).setScreenPos(new Vector4f(-1, -1, 0, 0)));
        uiSprites.add((UiSprite) new UiSprite(canvas, Item.STONE_PEBBLE.sprite).setScreenPos(new Vector4f(0, 0, 1, 1)).setKeepProportions(false));
        uiSprites.add((UiSprite) new UiSprite(canvas, Item.CHIP.sprite).setScreenPos(new Vector4f(0.25f, -1, 0.75f, 0)));
    }

    @Override
    public void update() {
//TODO: УБРАТЬ ОТСЮДА НАХУЙ!!!!!!!!



//        updateContents();
//
//        if (isDirty) {
//            redrawSidePanel();
//        }
    }

    public void updateContents() {
        contents = connectedInventory.getContents();

        isDirty = true;
    }

    private void redrawSidePanel() {
        //TODO: не нужно полностью очищать лист
        uiSprites.clear();
        //TODO: убрать counter
        int counter = 0;
        for (Map.Entry<Item, Integer> currItems : contents.entrySet()) {

            //TODO: не вижу смысла сначала передавать конвас в конструктор, а затем ему же передавать этот объект. Нужно что-то с этим придумать
            UiSprite uiSprite = (UiSprite) new UiSprite(canvas, currItems.getKey().sprite).setScreenPos(new Vector4f(-1, 0.5f - counter * 0.05f - 0.05f, -0.95f, 0.5f - counter * 0.05f));
//            canvas.addUiElement(uiSprite);
            uiSprites.add(uiSprite);

            counter++;
        }

        isDirty = false;
    }
}
