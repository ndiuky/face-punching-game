package engine.component;

import component.Item;
import engine.object.GameObject;
import engine.render.DebugDraw;
import engine.render.Window;
import engine.util.Color;
import engine.util.EnginePreferences;

import java.util.List;

public class CItemPickUp extends Component {

    private float pickUpRadius = 2;

    @Override
    public void update(float dt) {
        DebugDraw.drawCircle(gameObject.transform.position, pickUpRadius, -1, Color.GREY1, 1, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH);
        List<GameObject> allGameObjectsInRadius = Window.getCurrentScene().getAllGameObjectsInRadius(gameObject.transform.position, pickUpRadius);

        for (GameObject go : allGameObjectsInRadius) {
            for (Component component : go.getAllComponents()) {
                if (component instanceof Item item){
                    item.setIsBeingPickedUp(gameObject.transform.position);
                }
            }
        }

    }

    public CItemPickUp setPickUpRadius(float pickUpRadius) {
        this.pickUpRadius = pickUpRadius;

        return this;
    }
}
