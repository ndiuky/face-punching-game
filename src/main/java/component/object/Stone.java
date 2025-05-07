package component.object;

import component.ItemDrop;
import engine.component.CSpriteRenderer;
import engine.component.Component;
import engine.component.ui.CPositionText;
import engine.component.ui.CTextRenderer;
import engine.object.Sprite;
import engine.render.Window;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.AssetPool;
import engine.util.Color;
import engine.util.Interactable;
import item.Item;
import item.ItemStack;
import org.joml.Vector2f;
import util.InteractionType;
import util.LootTable;

import java.util.List;
import java.util.Random;

import static engine.util.EngineUtils.inverseLerpUnclamped;
import static util.InteractionType.ATTACK;

public class Stone extends Component implements Interactable {

    private int hp = 10;

    private boolean wasJustHit;

    private float currentHitAnimationTime = 0;

    private static final float HIT_ANIMATION_DURATION = 0.5f;

    private static final float MAX_Y_HIT_ANIMATION_DELTA = 0.6f;

    //TODO: хотел сделать цвет камня белым когда его бьёшь, но чет лень стало это нормально реализовывать, поэтому потом как-нибудь сделай пж
    private CSpriteRenderer stoneSR;

    private Vector2f originalScale;

    Random random = new Random();

    private LootTable lootTable = LootTable.of(
            ItemDrop.generateForItemStack(new ItemStack(Item.STONE_PEBBLE, 2)),
            ItemDrop.generateForItem(Item.SCREW),
            ItemDrop.generateForItem(Item.NUT)
    );

    @Override
    public void start() {
        originalScale = new Vector2f(gameObject.transform.scale);

        stoneSR = new CSpriteRenderer()
                .setSprite(new Sprite().setTexture(AssetPool.getTexture("stone")))
                .setIsClickable(true);

        gameObject.addComponent(stoneSR);
    }

    @Override
    public void update(float dt) {
        if (wasJustHit) {
            playHitAnimation(dt);
        }
    }

    private void reduceHp(int reduceBy) {
        hp -= reduceBy;
        if (hp <= 0) {
            destroyStone();
        }
    }

    private void destroyStone() {
        for (ItemDrop itemDrop : lootTable.getContent()) {
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
    public List<InteractionType> getInteractionTypes() {
        return List.of(ATTACK);
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
