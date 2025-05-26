package component.object;

import component.ItemDrop;
import engine.component.CSpriteRenderer;
import engine.render.Window;
import org.joml.Vector2f;
import registry.ItemType;
import registry.TextureType;
import util.InteractionType;
import util.ItemStack;

import static engine.util.EngineUtils.RANDOM;

// TODO: это тестовый класс, чтобы проверить несколько рандомных вещей. В целом можно под что-то более интересное переделать
public class PickableTest extends InteractableComponent {

    private final float rotationSpeed = RANDOM.nextFloat(-25, 25);

    private final ItemStack drop = new ItemStack(ItemType.CHIP_LOCKED, 69);

    @Override
    public void start() {
        gameObject.addComponent(new CSpriteRenderer(TextureType.BONUS_BUTTON));

        initInteractableComponent();
    }

    @Override
    public void update(final float dt) {
        playAnimation(dt);
        if (AnimationState.NONE.equals(currAnimationState)) {
            gameObject.transform.rotation += dt * rotationSpeed;
        }
    }

    @Override
    public InteractionType[] getAllowedInteractionTypes() {
        return new InteractionType[]{InteractionType.USE};
    }

    @Override
    protected void afterInteraction(final InteractionType interactionType) {
        if (InteractionType.USE.equals(interactionType)) {
            final ItemDrop itemDrop = ItemDrop.generateForItemStack(drop);
            itemDrop.gameObject.transform.position = new Vector2f(gameObject.transform.position);
            itemDrop.applyInitialImpulse(new Vector2f(RANDOM.nextFloat(-0.5f, 0.5f), RANDOM.nextFloat(-0.5f, 0.5f)));
            Window.getCurrentScene().addGameObjectToScene(itemDrop.gameObject);

            gameObject.destroy();

            return;
        }
    }
}
