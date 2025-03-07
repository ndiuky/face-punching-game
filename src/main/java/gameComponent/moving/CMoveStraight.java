package gameComponent.moving;

import controllerAttiribute.ActionType;
import engine.util.listener.MouseListener;
import gameComponent.GameComponent;
import org.joml.Vector2f;
import сomponent.entity.Entity;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class CMoveStraight extends GameComponent {

    @Override
    public void init(Entity entity) {
        actionType = ActionType.MOVE;
        isPassive = false;
        this.entity = entity;
    }

    @Override
    public void execute() {

    }

//    private void goToTarget(final float dt) {
//        final Vector2f dir = new Vector2f(targetPos).sub(new Vector2f(gameObject.transform.position));
//
////        System.out.println("GOING TO " + (int)targetPos.x + ", " + (int)targetPos.y + ", curr pos " + (int)gameObject.transform.position.x + ", " + (int)gameObject.transform.position.y);
//
//        float distance = new Vector2f(dir).absolute().length();
//
//        if (distance == 0f ||distance < new Vector2f(dir).normalize().mul(movementSpeed * dt).absolute().length()) {
//            gameObject.transform.position = targetPos;
//
//            if (positions.isEmpty()) {
//                isAtTargetPos = true;
//                return;
//            }
//
//            targetPos = positions.poll();
//        } else {
//            gameObject.transform.position.add(new Vector2f(dir).normalize().mul(movementSpeed * dt));
//        }
//    }

}
