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
import registry.Interaction;
import util.LootTable;

import java.util.List;
import java.util.Random;

import static registry.Interaction.ATTACK;
import static util.Utils.inverseLerpUnclamped;

public class Stone extends Component implements Interactable {

    private static final int MAX_HP = 10;

    private int hp = MAX_HP;

    private boolean wasJustHit;

    private float currentHitAnimationTime = 0;

    private static final float HIT_ANIMATION_DURATION = 0.5f;

    private static final float MAX_Y_HIT_ANIMATION_DELTA = 0.6f;

    //TODO: хотел сделать цвет камня белым когда его бьёшь, но чет лень стало это нормально реализовывать, поэтому потом как-нибудь сделай пж
    private CSpriteRenderer stoneSR;

    private Vector2f originalScale;

    Random random = new Random();

    private LootTable lootTable = LootTable.of(
            new ItemStack(ItemType.STONE_PEBBLE, 2),
            new ItemStack(ItemType.SCREW),
            new ItemStack(ItemType.NUT)
    );

    @Override
    public void start() {
        originalScale = new Vector2f(gameObject.transform.scale);

        stoneSR = new CSpriteRenderer(TextureType.STONE).setAnchorPoint(0, -0.35f);

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
        for (ItemDrop itemDrop : lootTable.getContentDropped()) {
            itemDrop.gameObject.transform.position = new Vector2f(gameObject.transform.position);
            itemDrop.applyInitialImpulse(new Vector2f(random.nextFloat(-1, 1), random.nextFloat(-1, 1)));
            Window.currentScene.addGameObjectToScene(itemDrop.gameObject);
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
    public void onInteraction(Interaction interaction) {
        if (interaction == ATTACK) {
            reduceHp(3);

            currentHitAnimationTime = 0;
            wasJustHit = true;
        }
    }
}
