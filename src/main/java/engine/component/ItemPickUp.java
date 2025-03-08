package engine.component;

import engine.render.DebugDraw;
import engine.util.Color;
import engine.util.EnginePreferences;

public class ItemPickUp extends Component{

    private float pickUpRadius;

    public ItemPickUp(final float pickUpRadius) {
        this.pickUpRadius = pickUpRadius;
    }

    @Override
    public void update(final float dt) {
//        DebugDraw.drawCircle(gameObject.transform.position, pickUpRadius, -1, Color.BLUE, 1, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH);
    }

    public ItemPickUp setPickUpRadius(final float pickUpRadius) {
        this.pickUpRadius = pickUpRadius;
        return this;
    }
}
