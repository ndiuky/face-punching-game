package component.entity;

import controllerAttiribute.ActionType;
import engine.event.KeyboardEventListener;
import engine.event.MouseEventListener;
import engine.util.listener.MouseListener;

import static controllerAttiribute.ActionType.ATTACK;
import static controllerAttiribute.ActionType.BLOCK;
import static controllerAttiribute.ActionType.BONUS;
import static controllerAttiribute.ActionType.MOVE;
import static engine.event.KeyboardEventProducer.listenToKeyboardPress;
import static engine.event.MouseEventProducer.listenToMousePress;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class Player extends Bot implements MouseEventListener, KeyboardEventListener {

    private ActionType currActionType = MOVE;

    @Override
    public void start() {
        listenToMousePress(GLFW_MOUSE_BUTTON_1, this);
        listenToMousePress(GLFW_MOUSE_BUTTON_2, this);

        listenToKeyboardPress(GLFW_KEY_Q, this);
        listenToKeyboardPress(GLFW_KEY_W, this);
        listenToKeyboardPress(GLFW_KEY_E, this);
        listenToKeyboardPress(GLFW_KEY_R, this);

        callForEachGameComponent(gc -> gc.init(this));
    }

    @Override
    public void update(final float dt) {
        callForEachGameComponent(gameComponent -> gameComponent.update(dt));
    }

    @Override
    public void onMousePress(final int button) {
        switch (currActionType) {
            case MOVE -> moveGC.executeMovement(button, MouseListener.getOrtho());
            case ATTACK -> attackGC.executeAttack(button, MouseListener.getGameObjectUnderCursor());
            case BLOCK -> blockGC.executeBlock(button, MouseListener.getRelativeAngle(gameObject.transform.position));
            case BONUS -> {
                throw new RuntimeException("Bonus execution is not implemented yet");
            }
        }
    }

    @Override
    public void onMouseRelease(final int button) {
    }

    @Override
    public void onKeyPress(final int key) {
        ActionType lastActionType = currActionType;

        switch (key) {
            case GLFW_KEY_Q -> {
                if (moveGC != null) {
                    currActionType = MOVE;
                }
            }
            case GLFW_KEY_W -> {
                if (attackGC != null) {
                    currActionType = ATTACK;
                }
            }
            case GLFW_KEY_E -> {
                if (blockGC != null) {
                    currActionType = BLOCK;
                }
            }
            case GLFW_KEY_R -> {
                if (bonusGC != null) {
                    currActionType = BONUS;
                }
            }
            default -> {
                return;
            }
        }

        if (!currActionType.equals(lastActionType)) {
            stopExecution(lastActionType);
        }
    }

    @Override
    public void onKeyRelease(final int key) {

    }
}
