package engine.component;

import engine.util.listener.KeyListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class CMoveWasd extends Component {

    private Vector2f moveDir = new Vector2f(0, 0);

    private float movenentSpeed = 10;

    private static final float MAGIC_NUMBER = 0.71f;

    @Override
    public void start() {

    }

    @Override
    public void update(float dt) {
        checkForMovement();

        gameObject.transform.position.add(new Vector2f(moveDir).mul(dt * movenentSpeed));
    }

    private void checkForMovement() {
        boolean wPressed = KeyListener.isKeyPressed(GLFW_KEY_W);
        boolean aPressed = KeyListener.isKeyPressed(GLFW_KEY_A);
        boolean sPressed = KeyListener.isKeyPressed(GLFW_KEY_S);
        boolean dPressed = KeyListener.isKeyPressed(GLFW_KEY_D);

        int xAxis = 0;
        int yAxis = 0;

        if (wPressed && sPressed) {
            yAxis = 0;
        } else if (wPressed) {
            yAxis = 1;
        } else if (sPressed) {
            yAxis = -1;
        }

        if (aPressed && dPressed) {
            xAxis = 0;
        } else if (aPressed) {
            xAxis = -1;
        } else if (dPressed) {
            xAxis = 1;
        }

        calculateNewDirection(xAxis, yAxis);
    }

    private void calculateNewDirection(int xAxis, int yAxis) {
        if (xAxis != 0 && yAxis != 0) {
            moveDir = new Vector2f(xAxis * MAGIC_NUMBER, yAxis * MAGIC_NUMBER);
        } else {
            moveDir = new Vector2f(xAxis, yAxis);
        }
    }
}
