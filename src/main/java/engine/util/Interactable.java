package engine.util;

import util.InteractionType;

import java.util.List;

public interface Interactable {

    List<InteractionType> getInteractionTypes();

    void onInteraction(InteractionType interactionType);
}
