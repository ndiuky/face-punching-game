package engine.component;

import engine.render.DebugDraw;
import engine.util.Color;
import engine.util.EnginePreferences;

public class CItemPickUp extends Component {

    private float pickUpRadius = 2;

    @Override
    public void update(float dt) {
        DebugDraw.drawCircle(gameObject.transform.position, pickUpRadius, -1, Color.GREY1, 1, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH);
    }

    public CItemPickUp setPickUpRadius(float pickUpRadius) {
        this.pickUpRadius = pickUpRadius;

        return this;
    }
}
