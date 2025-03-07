package game_component.moving;

import component.entity.Bot;
import controllerAttiribute.ActionType;
import game_component.GameComponent;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class CMoveArc extends GameComponent {

    private Vector2f[] path;

    private float movementSpeed = 400;

    private float arcUpperPointDistance = 1;

    private byte arcSegments = 20;

    @Override
    public void init(final Bot bot) {
        actionType = ActionType.MOVE;
        this.bot = bot;
    }

    @Override
    public void update(final float dt) {
        if (path != null) {
            followPath();
        }
    }

    @Override
    public void onClick(final int button, final Vector2f clickPos) {
        if (button == GLFW_MOUSE_BUTTON_1) {
            path = calculatePath(clickPos, true);
        } else if (button == GLFW_MOUSE_BUTTON_2) {
            path = calculatePath(clickPos, false);
        }
    }

    private void followPath() {


        if (true) {
            path = null;
        }
    }

    private Vector2f[] calculatePath(final Vector2f destination, final boolean isLeftSided) {
        final float iterationStep = 1 / arcSegments;
        final Vector2f botPos = new Vector2f(bot.gameObject.transform.position);

        Vector2f BPoint = calculateBPoint(botPos, destination);

        for (int i = 0; i < arcSegments; i++) {
            float currTime = iterationStep * i;

            Vector2f interpolatedAB = new Vector2f(destination.sub(botPos).mul(currTime).add(botPos));

            Vector2f interpolatedBC = new Vector2f(destination.sub(botPos).mul(currTime).add(botPos));
        }
    }

    private Vector2f calculateBPoint(final Vector2f botPos, final Vector2f destination) {
        return null;
    }
}
