package component;

import engine.component.CItemPickUp;
import engine.component.Component;
import engine.component.ui.CTextRenderer;
import engine.object.Prefab;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.EngineUtils;
import org.joml.Math;
import org.joml.Vector2f;
import registry.ItemType;
import util.ItemStack;

import java.util.Random;

import static java.lang.Math.pow;

//TODO: чем больше размер стака, тем больше скейл итема
//TODO: убрать item из рендера в pickingShader
public class ItemDrop extends Component {

    private static final float FLYING_ACCELERATION_RATE = 20;

    private static final float MAX_SUCKING_ANGLE_DELTA = 45;

    private static final float WOBBLE_SIZE_DELTA = 0.08f;

    private static final float SLIDE_TIME = 0.5f;

    private static final float ROTATION_TIME = 1f;

    private final float wobbleOffset = new Random().nextFloat(1);

    private ItemStack containingStack;

    private Vector2f initialPos;

    private Vector2f initialScale;

    private boolean isFullyStopped = false;

    private Vector2f slideDir;

    private float currSlidingTime;

    private CItemPickUp suckedByComponent;

    private boolean isBeingPickedUp = false;

    private float currFlyingTime = 0;

    private boolean isDroppedManually = false;

    private ItemDrop(ItemType item) {
        containingStack = new ItemStack(item);
    }

    private ItemDrop(ItemStack itemStack) {
        containingStack = itemStack;
    }

    public static ItemDrop generateForItem(ItemType item) {
        ItemDrop itemDrop = new ItemDrop(item);
        Prefab.generateSpriteObject(item.sprite, item.dropSizeMultiplier, 1, item.name())
                .addComponent(itemDrop);
        return itemDrop;
    }

    public static ItemDrop generateForItemStack(ItemStack itemStack) {
        if (itemStack.getStackSize() == 1) {
            return generateForItem(itemStack.getItem());
        }

        ItemDrop itemDrop = new ItemDrop(itemStack);
        Prefab.generateSpriteObject(itemStack.getItem().sprite, itemStack.getItem().dropSizeMultiplier, 1, itemStack.getItem().name())
                .addComponent(itemDrop)
                .addComponent(
                        new CTextRenderer(
                                new Text(String.valueOf(itemStack.getStackSize()))
                                        // TODO: offset кривой. Фиксить нужно будет после имплементации Anchor point
                                        .setOffset(new Vector2f(-0.25f * itemStack.getItem().dropSizeMultiplier))
                                        .setAlignment(Alignment.TOP_LEFT)
                        )
                );
        return itemDrop;
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
        Vector2f dir = new Vector2f(suckedByComponent.gameObject.transform.position).sub(gameObject.transform.position);

        currFlyingTime += dt;

        if (dir.lengthSquared() < 0.1f) {
            suckedByComponent.onPickupComplete(containingStack);
            gameObject.destroy();
        } else {
            Vector2f dirNormalized = dir.normalize();
            float desiredRotation = dirNormalized.dot(new Vector2f(0, 1)) * MAX_SUCKING_ANGLE_DELTA;

            gameObject.transform.position.add(dirNormalized.mul(currFlyingTime * dt * FLYING_ACCELERATION_RATE));
            gameObject.transform.rotation = Math.lerp(gameObject.transform.rotation, desiredRotation, Math.clamp(0, 1, 5 * dt));
        }
    }

    private void performInitialSlide(float dt) {
        float factor = currSlidingTime >= SLIDE_TIME ? 1 : (float) (1 - pow(2, -15 * currSlidingTime));
        float rotationFactor;
        if (currSlidingTime == 0) {
            rotationFactor = 0;
        } else {
            rotationFactor = currSlidingTime >= ROTATION_TIME ? 1 : (float) (1 - pow(2, -10 * currSlidingTime));
        }

        gameObject.transform.position = new Vector2f(initialPos).add(new Vector2f(slideDir).mul(factor));
        gameObject.transform.rotation = rotationFactor * MAX_SUCKING_ANGLE_DELTA;

        currSlidingTime += dt;

        if (factor == 1 && rotationFactor == 1) {
            isFullyStopped = true;
            currSlidingTime = 0;
            initialPos = gameObject.transform.position;
        }
    }

    private void wobble() {
        float sizeOffset = Math.sin((EngineUtils.time() + wobbleOffset) * 2) * WOBBLE_SIZE_DELTA;

        gameObject.transform.scale = new Vector2f(sizeOffset + initialScale.x, sizeOffset + initialScale.y);
    }

    public void setIsBeingPickedUp(CItemPickUp byComponent) {
        if (isDroppedManually && !isFullyStopped) {
            return;
        }

        isBeingPickedUp = true;

        this.suckedByComponent = byComponent;
    }

    public boolean isBeingPickedUp() {
        return isBeingPickedUp;
    }

    public boolean isDroppedManually() {
        return isDroppedManually;
    }

    public ItemDrop setDroppedManually(final boolean droppedManually) {
        isDroppedManually = droppedManually;

        return this;
    }
}
