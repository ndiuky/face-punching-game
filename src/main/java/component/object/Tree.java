package component.object;

import engine.component.CSpriteRenderer;
import org.joml.Vector2f;
import registry.ItemType;
import registry.TextureType;
import util.InteractionType;
import util.ItemStack;
import util.LootTable;

import java.util.Arrays;

import static util.InteractionType.ATTACK;

public class Tree extends BreakableComponent {

    @Override
    public void start() {
        gameObject.transform.scale = new Vector2f(3);
        gameObject.addComponent(new CSpriteRenderer(TextureType.TREE).setAnchorPoint(0, -0.45f));
        animationDuration = 1;

        initBreakableComponent(10,
                LootTable.of(
                        Arrays.stream(ItemType.values())
                                .map(ItemStack::new)
                                .toArray(ItemStack[]::new))
        );
    }

    @Override
    public void update(float dt) {
        playAnimation(dt);
    }

    @Override
    public InteractionType[] getAllowedInteractionTypes() {
        return new InteractionType[]{ATTACK};
    }

    @Override
    public void afterInteraction(InteractionType interactionType) {
        if (interactionType == ATTACK) {
            reduceHp(3);
        }
    }
}
