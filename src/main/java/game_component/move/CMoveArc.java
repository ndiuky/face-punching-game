package game_component.move;

import component.entity.Bot;
import org.joml.Vector2f;

import java.util.ArrayDeque;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

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
                calculatePath(position, true);
            } else if (button == GLFW_MOUSE_BUTTON_2) {
                calculatePath(position, false);
            }
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

    //TODO: удалить debugMode, либо переделать его в более геймплейно обоснованный вид
    private void calculatePath(final Vector2f destination, final boolean isLeftSided) {

        final float botX = bot.gameObject.transform.position.x;
        final float botY = bot.gameObject.transform.position.y;

        final float destinationX = destination.x;
        final float destinationY = destination.y;

        final Vector2f BPoint = calculateBPoint(botX, botY, destinationX, destinationY, isLeftSided);

        float BX = BPoint.x;
        float BY = BPoint.y;

        final float iterationStep = 1f / arcSegments;
        float currTime, currTimeSquared, oneMinusCurrTime, oneMinusCurrTimeSquared;
        for (int i = 0; i <= arcSegments; i++) {
            currTime = iterationStep * i;
            oneMinusCurrTime = 1 - currTime;
            oneMinusCurrTimeSquared = oneMinusCurrTime * oneMinusCurrTime;
            currTimeSquared = currTime * currTime;

            path.add(new Vector2f(
                    oneMinusCurrTimeSquared * botX + 2 * oneMinusCurrTime * currTime * BX + currTimeSquared * destinationX,
                    oneMinusCurrTimeSquared * botY + 2 * oneMinusCurrTime * currTime * BY + currTimeSquared * destinationY
            ));
            isAtTargetDestination = false;
        }
    }

    private Vector2f calculateBPoint(final float botX, final float botY, final float destinationX, final float destinationY, final boolean isLeftSided) {
        float fromToXDivided = (destinationX - botX) * approachAngle;
        float fromToYDivided = (destinationY - botY) * approachAngle;

        final float sideAdjustment = (isLeftSided == (botY > destinationY) ? 1 : -1) * middlePointDistance;

        return new Vector2f(
                botX + fromToXDivided + fromToYDivided * sideAdjustment,
                botY + fromToYDivided - fromToXDivided * sideAdjustment
        );
    }
}
