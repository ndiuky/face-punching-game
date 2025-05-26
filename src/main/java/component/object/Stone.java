package component.object;

import engine.component.CSpriteRenderer;
import registry.ItemType;
import registry.TextureType;
import util.InteractionType;
import util.ItemStack;
import util.LootTable;

import static util.InteractionType.ATTACK;

public class Stone extends BreakableComponent {

    @Override
    public void start() {
        gameObject.addComponent(new CSpriteRenderer(TextureType.STONE).setAnchorPoint(0, -0.35f));

        initBreakableComponent(5,
                LootTable.of(
                        new ItemStack(ItemType.STONE_PEBBLE, 2),
                        new ItemStack(ItemType.SCREW),
                        new ItemStack(ItemType.NUT)
                ));
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
