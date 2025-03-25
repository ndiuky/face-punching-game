package gameComponent.move;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

@Deprecated
public class CMoveStraight extends MoveGameComponent {

    private Vector2f destination;

    private float movementSpeed = 400;

    @Override
    public void update(float dt) {
        if (destination != null) {
            goToTarget(dt);
        }
    }

    @Override
    public void executeMovement(int button, Vector2f position) {
        if (button == GLFW_MOUSE_BUTTON_1) {
            destination = position;
        }
    }

    @Override
    public void stopExecution() {
        destination = null;
    }

    //TODO: не вычислять каждый раз направление движения, а сделать это один раз перед началом движения
    private void goToTarget(float dt) {
        Vector2f dir = new Vector2f(destination).sub(new Vector2f(bot.gameObject.transform.position));

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(movementSpeed * dt).absolute().length()) {
            bot.gameObject.transform.position = destination;

            destination = null;
        } else {
            bot.gameObject.transform.position.add(new Vector2f(dir).normalize().mul(movementSpeed * dt));
        }
    }
}
