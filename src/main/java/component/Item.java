package component;

import engine.component.Component;
import engine.object.Prefab;
import engine.object.Sprite;
import engine.util.AssetPool;
import engine.util.EngineUtils;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.Random;

public class Item extends Component {

    private Vector2f initialPos;

    private Vector2f initialScale;

    private float sizeDelta = 5f;

    private float wobbleOffset = new Random().nextFloat(1);

    private boolean isFullyStopped = false;

    private Vector2f slideDir;

    private final float SLIDE_TIME = 0.5f;

    private float currSlidingTime;

    //TODO:
//    private String itemName;

    public static Item generateFromTexture(String textureName) {
        Item item = new Item();
        Prefab.generateSpriteObject(new Sprite().setTexture(AssetPool.getTexture(textureName)), 25, 25, 1)
                .addComponent(item);
        return item;
    }

    @Override
    public void start() {
        initialPos = new Vector2f(gameObject.transform.position);
        initialScale = new Vector2f(gameObject.transform.scale);
    }

    @Override
    public void update(float dt) {
        if (!isFullyStopped) {
            performInitialSlide(dt);
        }
        wobble();
    }

    public void applyInitialImpulse(Vector2f dir) {
        isFullyStopped = false;
        slideDir = dir;
        initialPos = new Vector2f(gameObject.transform.position);
    }

    private void performInitialSlide(float dt) {
        float factor = currSlidingTime >= SLIDE_TIME ? 1 : (float) (1 - java.lang.Math.pow(2, -50 * currSlidingTime));

        gameObject.transform.position = new Vector2f(initialPos).add(new Vector2f(slideDir).mul(factor));

        currSlidingTime += dt;

        if (factor == 1) {
            isFullyStopped = true;
            currSlidingTime = 0;
            initialPos = gameObject.transform.position;
        }
    }

    private void wobble() {
        float sizeOffset = Math.sin((EngineUtils.time() + wobbleOffset) * 2) * sizeDelta;

        gameObject.transform.scale = new Vector2f(sizeOffset + initialScale.x, sizeOffset + initialScale.y);
    }
}
