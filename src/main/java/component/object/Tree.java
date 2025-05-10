package component.object;

import component.ItemDrop;
import engine.component.CSpriteRenderer;
import engine.component.Component;
import engine.render.Window;
import engine.util.Interactable;
import registry.ItemType;
import registry.TextureType;
import util.ItemStack;
import org.joml.Vector2f;
import util.InteractionType;
import util.LootTable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static engine.util.EngineUtils.inverseLerpUnclamped;
import static util.InteractionType.ATTACK;

public class Tree extends Component implements Interactable {

    private int hp = 15;

    private boolean wasJustHit;

    private float currentHitAnimationTime = 0;

    private static final float HIT_ANIMATION_DURATION = 0.5f;

    private static final float MAX_Y_HIT_ANIMATION_DELTA = 0.6f;

    private CSpriteRenderer treeSR;

    private Vector2f originalScale;

    Random random = new Random();

    private LootTable lootTable = LootTable.of(
            Arrays.stream(ItemType.values())
                    .map(ItemStack::new)
                    .toArray(ItemStack[]::new)
    );

    @Override
    public void start() {
        gameObject.transform.scale = new Vector2f(3);
        originalScale = new Vector2f(gameObject.transform.scale);

        treeSR = new CSpriteRenderer(TextureType.TREE).setAnchorPoint(0, -0.45f);

        gameObject.addComponent(treeSR);
    }

    @Override
    public void update(float dt) {
        if (wasJustHit) {
            playHitAnimation(dt);
        }
    }

    @Override
    public List<InteractionType> getInteractionTypes() {
        return List.of();
    }

    private void reduceHp(int reduceBy) {
        hp -= reduceBy;
        if (hp <= 0) {
            destroyStone();
        }
    }

    private void destroyStone() {
        for (ItemDrop itemDrop : lootTable.getContentDropped()) {
            itemDrop.gameObject.transform.position = new Vector2f(gameObject.transform.position);
            itemDrop.applyInitialImpulse(new Vector2f(random.nextFloat(-1, 1), random.nextFloat(-1, 1)));
            Window.getCurrentScene().addGameObjectToScene(itemDrop.gameObject);
        }
        gameObject.destroy();
    }

    private void playHitAnimation(float dt) {
        currentHitAnimationTime += dt;
        float lerpResult = inverseLerpUnclamped(0, HIT_ANIMATION_DURATION, currentHitAnimationTime);

        if (lerpResult >= 1) {
            gameObject.transform.scale = new Vector2f(originalScale);
            currentHitAnimationTime = 0;
            wasJustHit = false;
        } else {
            gameObject.transform.scale = new Vector2f(originalScale.x, (float) (originalScale.y + Math.pow(2, -10 * lerpResult) * MAX_Y_HIT_ANIMATION_DELTA));
        }
    }

    @Override
    public void onInteraction(InteractionType interactionType) {
        if (interactionType == ATTACK) {
            reduceHp(3);

            currentHitAnimationTime = 0;
            wasJustHit = true;
        }
    }
}
