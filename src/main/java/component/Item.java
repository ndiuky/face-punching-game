package component;

import engine.component.Component;
import engine.object.Prefab;
import engine.object.Sprite;
import engine.util.AssetPool;
import engine.util.EngineUtils;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.Random;

//TODO: убрать item из рендера в pickingShader
//TODO: пусть предметы немного поворачиваются в зависимости от скорости и направления их движения (например когда предмет летит к игроку)
public class Item extends Component {

    private Vector2f initialPos;

    private Vector2f initialScale;

    private static final float sizeDelta = 0.08f;

    private static final float wobbleOffset = new Random().nextFloat(1);

    private boolean isFullyStopped = false;

    private Vector2f slideDir;

    private static final float SLIDE_TIME = 0.5f;

    private float currSlidingTime;

    private Vector2f suckerPos;

    private boolean isBeingPickedUp = false;

    private float currFlyingTime = 0;

    private final float flyingAccelerationRate = 30;

    //TODO:
//    private String itemName;

    public static Item generateFromTexture(String textureName) {
        Item item = new Item();
        Prefab.generateSpriteObject(new Sprite().setTexture(AssetPool.getTexture(textureName)), 0.4f, 0.4f, 1)
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
        } else if (isBeingPickedUp) {
            flyTowardsSucker(dt);
        }
        wobble();
    }

    public void applyInitialImpulse(Vector2f dir) {
        isFullyStopped = false;
        slideDir = dir;
        initialPos = new Vector2f(gameObject.transform.position);
    }

    private void flyTowardsSucker(float dt) {
        Vector2f dir = new Vector2f(suckerPos).sub(gameObject.transform.position);

        currFlyingTime += dt;

        if (dir.lengthSquared() < 0.1f) {
            gameObject.destroy();
        } else {
            gameObject.transform.position.add(dir.normalize().mul(currFlyingTime * dt * flyingAccelerationRate));
        }
    }

    private void performInitialSlide(float dt) {
        float factor = currSlidingTime >= SLIDE_TIME ? 1 : (float) (1 - java.lang.Math.pow(2, -15 * currSlidingTime));

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

    public void setIsBeingPickedUp(Vector2f suckerPos) {
        isBeingPickedUp = true;

        this.suckerPos = suckerPos;
    }
}
