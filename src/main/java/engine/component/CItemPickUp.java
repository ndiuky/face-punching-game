package engine.component;

import component.ItemDrop;
import engine.object.GameObject;
import engine.render.DebugDraw;
import engine.render.Window;
import engine.util.Color;
import engine.util.EnginePreferences;
import org.joml.Vector2f;
import util.ItemStack;

import java.util.List;

public class CItemPickUp extends Component {

    public float pickUpRadius = 2;

    @Override
    public void update(float dt) {
        DebugDraw.drawCircle(gameObject.transform.position, pickUpRadius, -1, Color.GREY1, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH);

        List<GameObject> allGameObjectsInRadius = Window.currentScene.getAllGameObjectsInRadius(gameObject.transform.position, pickUpRadius);

        for (GameObject go : allGameObjectsInRadius) {
            for (Component component : go.components) {
                if (component instanceof ItemDrop itemDrop && !itemDrop.isBeingPickedUp()) {
                    itemDrop.setIsBeingPickedUp(this);
                }
            }
        }
    }

    public void onPickupComplete(ItemStack pickedItem) {
        final List<CInventory> objectInventories = gameObject.getComponents(CInventory.class);
        if (objectInventories == null) {
            return;
        }

        objectInventories.getFirst().addItemStack(pickedItem);
    }
}
