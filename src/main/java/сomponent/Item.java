package сomponent;

import engine.component.Component;
import engine.util.Utils;
import org.joml.Math;
import org.joml.Vector2f;

public class Item extends Component {

    private Vector2f originalPos;

    private float yOffset;

    private float yDelta = 20;

    @Override
    public void start() {
        originalPos = new Vector2f(gameObject.transform.position);
    }

    @Override
    public void update(final float dt) {
        wobble(dt);
    }

    private void wobble(final float dt){
        yOffset = Math.sin(Utils.time()*2) * yDelta;

        gameObject.transform.position = new Vector2f(originalPos).add(new Vector2f(0, yOffset));
    }
}
