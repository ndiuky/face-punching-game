package engine.component;

import component.object.InteractableComponent;
import engine.event.MouseEventListener;
import engine.object.GameObject;
import engine.util.listener.MouseListener;
import util.InteractionType;

import static engine.event.MouseEventProducer.listenToMousePress;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class CRmbInteraction extends Component implements MouseEventListener {

    @Override
    public void start() {
        listenToMousePress(GLFW_MOUSE_BUTTON_2, this);
    }

    @Override
    public void update(final float dt) {
        super.update(dt);
    }

    @Override
    public void onMousePress(final int button) {
        if (button == GLFW_MOUSE_BUTTON_2) {
            tryInteractWithGameobjectUnderCursor();
        }
    }

    @Override
    public void onMouseRelease(final int button) {
    }

    private void tryInteractWithGameobjectUnderCursor() {
        final GameObject gameobjectUnderCursor = MouseListener.getGameObjectUnderCursor();
        if (gameobjectUnderCursor == null) {
            return;
        }

        for (Component currComponent : gameobjectUnderCursor.getAllComponents()) {
            if (currComponent instanceof InteractableComponent interactableComponent) {
                interactableComponent.baseOnInteraction(InteractionType.USE);
            }
        }
    }
}
