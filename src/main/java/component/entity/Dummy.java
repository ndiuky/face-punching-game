package component.entity;

import engine.util.Interactable;
import util.InteractionType;

import java.util.List;

public class Dummy extends Entity implements Interactable {

    @Override
    public void start() {
        gameObject.setInteractable(true);

    }

    @Override
    public void update(float dt) {
        if (isKnocked) {
            flyFromKnock(dt);
        }
    }

    @Override
    public List<InteractionType> getInteractionTypes() {
        return List.of(InteractionType.ATTACK);
    }

    @Override
    public void onInteraction(InteractionType interactionType) {
        isKnocked = true;
    }
}
