package engine.util;

import registry.Interaction;

import java.util.List;

public interface Interactable {

//    List<Interaction> getInteractionTypes();

    void onInteraction(Interaction interaction);
}
