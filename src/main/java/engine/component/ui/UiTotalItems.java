package engine.component.ui;

import engine.component.CInventory;
import registry.ItemType;
import org.joml.Vector4f;

import java.util.EnumMap;
import java.util.Map;

public class UiTotalItems extends UiElement {

    private final CInventory connectedInventory;

    private EnumMap<ItemType, Integer> contents = new EnumMap<>(ItemType.class);

    private boolean isDirty = true;

    public UiTotalItems(Canvas canvas, CInventory connectedInventory) {
        super(canvas);

        this.connectedInventory = connectedInventory;
    }

    @Override
    protected void update() {
//TODO: УБРАТЬ ОТСЮДА НАХУЙ!!!!!!!!
        updateContents();

        if (isDirty) {
            redrawSidePanel();
        }
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
        for (Map.Entry<ItemType, Integer> currItems : contents.entrySet()) {

            //TODO: не вижу смысла сначала передавать конвас в конструктор, а затем ему же передавать этот объект. Нужно что-то с этим придумать
            UiSprite uiSprite = (UiSprite) new UiSprite(canvas, currItems.getKey().sprite).setScreenPos(new Vector4f(-1, 1 - (counter + 1) * 0.1f, -0.9f, 1 - counter * 0.1f));
            uiSprites.add(uiSprite);

            counter++;
        }

        isDirty = false;
    }
}
