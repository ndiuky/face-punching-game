package component.entity;

import gameComponent.CFistAttackRandom;
import gameComponent.CMoveWasd;
import engine.component.Component;
import engine.event.KeyboardEventListener;
import engine.event.MouseEventListener;

import java.util.List;

import static engine.event.MouseEventProducer.listenToMousePress;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class Player extends Bot implements MouseEventListener, KeyboardEventListener {

    private static final List<Component> REQUIRED_COMPONENTS = List.of(new CMoveWasd(), new CFistAttackRandom());

    @Override
    public void start() {
        for (Component requiredComponent : REQUIRED_COMPONENTS) {
            if (gameObject.getComponent(requiredComponent.getClass()) == null){
                gameObject.addComponent(requiredComponent);
            }
        }

        listenToMousePress(GLFW_MOUSE_BUTTON_1, this);
        listenToMousePress(GLFW_MOUSE_BUTTON_2, this);
    }

    @Override
    public void update(float dt) {

    }


    @Override
    public void onMousePress(int button) {
//        attackGC.executeAttack(button, MouseListener.getGameObjectUnderCursor());
//        switch (currActionType) {
//            case MOVE -> moveGC.executeMovement(button, MouseListener.getOrtho());
//            case ATTACK -> attackGC.executeAttack(button, MouseListener.getGameObjectUnderCursor());
//            case BLOCK -> blockGC.executeBlock(button, MouseListener.getRelativeAngle(gameObject.transform.position));
//            case BONUS -> {
//                throw new RuntimeException("Bonus execution is not implemented yet");
//            }
//        }
    }

    @Override
    public void onMouseRelease(int button) {
    }

    @Override
    public void onKeyPress(int key) {
    }

    @Override
    public void onKeyRelease(int key) {
    }
}
