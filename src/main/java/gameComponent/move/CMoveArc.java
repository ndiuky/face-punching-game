package gameComponent.move;

import org.joml.Vector2f;

import java.util.ArrayDeque;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static util.Utils.calculatePath;

public class CMoveArc extends MoveGameComponent {

    private ArrayDeque<Vector2f> path = new ArrayDeque<>();

    private float movementSpeed = 400;

    //Angle from 0 to 1
    private float approachAngle = 0.5f;

    private float middlePointDistance = 1;

    private byte arcSegments = 16;

    Vector2f currentDestination;

    private boolean isAtTargetDestination = true;

    @Override
    public void update(final float dt) {
        if (!isAtTargetDestination) {
            followPath(dt);
        }
    }

    @Override
    public void executeMovement(final int button, final Vector2f position) {
        if (path.isEmpty()) {

            if (button == GLFW_MOUSE_BUTTON_1) {
                path = calculatePath(bot.gameObject.transform.position, position, true, arcSegments, approachAngle, middlePointDistance);
            } else if (button == GLFW_MOUSE_BUTTON_2) {
                path = calculatePath(bot.gameObject.transform.position, position, false, arcSegments, approachAngle, middlePointDistance);
            }

            isAtTargetDestination = false;
        }
    }

    @Override
    public void stopExecution() {
        path.clear();
        currentDestination = null;
        isAtTargetDestination = true;
    }

    private void followPath(final float dt) {
        if (currentDestination == null) {
            currentDestination = path.pop();
        }

        final Vector2f dir = new Vector2f(currentDestination).sub(new Vector2f(bot.gameObject.transform.position));

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(movementSpeed * dt).absolute().length()) {
            if (path.isEmpty()) {
                bot.gameObject.transform.position = currentDestination;
                isAtTargetDestination = true;
                return;
            }

            currentDestination = path.pop();
            followPath(dt);
        } else {
            bot.gameObject.transform.position.add(new Vector2f(dir).normalize().mul(movementSpeed * dt));
        }
    }
}
