package engine.component;

public class CItemPickUp extends Component {

    private float pickUpRadius;

    public CItemPickUp(float pickUpRadius) {
        this.pickUpRadius = pickUpRadius;
    }

    @Override
    public void update(float dt) {
//        DebugDraw.drawCircle(gameObject.transform.position, pickUpRadius, -1, Color.BLUE, 1, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH);
    }

    public CItemPickUp setPickUpRadius(float pickUpRadius) {
        this.pickUpRadius = pickUpRadius;
        return this;
    }
}
