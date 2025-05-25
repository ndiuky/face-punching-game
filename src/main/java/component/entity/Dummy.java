package component.entity;

import engine.util.Interactable;
import registry.Interaction;

import java.util.List;

public class Dummy extends Entity implements Interactable {

    @Override
    public void start() {
        gameObject.isInteractable = true;
    }

    @Override
    public void update(float dt) {
        if (isKnocked) {
            flyFromKnock(dt);
        }
    }

    @Override
    public void onInteraction(Interaction interaction) {
        isKnocked = true;
    }
}
