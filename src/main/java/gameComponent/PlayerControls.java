package gameComponent;

import engine.component.Component;
import engine.util.listener.InputEventListener;
import engine.util.listener.MouseListener;
import org.joml.Vector2f;

import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class PlayerControls extends Component implements InputEventListener {

    private float xDir, yDir;

    private float movementSpeed = 400;

    private transient Vector2f targetPos = new Vector2f();

    private transient boolean isAtTargetPos = true;

//    private transient PriorityQueue<Action> actions;

    //TODO: заменить очередь векторов на очередь Actions
    private transient Queue<Vector2f> positions = new ArrayDeque<>();

    @Override
    public void start() {
        MouseListener.listenToPress(GLFW_MOUSE_BUTTON_1, this);
        MouseListener.listenToRelease(GLFW_MOUSE_BUTTON_2, this);
    }

    @Override
    public void update(final float dt) {

        if (!isAtTargetPos) {
            goToTarget(dt);
        }
    }

    private void executeNextAction(){
        //метод для выполнения следующего действия в очереди
    }

    private void goToTarget(final float dt) {
        final Vector2f dir = new Vector2f(targetPos).sub(new Vector2f(gameObject.transform.position));

//        System.out.println("GOING TO " + (int)targetPos.x + ", " + (int)targetPos.y + ", curr pos " + (int)gameObject.transform.position.x + ", " + (int)gameObject.transform.position.y);

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f ||distance < new Vector2f(dir).normalize().mul(movementSpeed * dt).absolute().length()) {
            gameObject.transform.position = targetPos;

            if (positions.isEmpty()) {
                isAtTargetPos = true;
                return;
            }

            targetPos = positions.poll();
        } else {
            gameObject.transform.position.add(new Vector2f(dir).normalize().mul(movementSpeed * dt));
        }
    }

    @Override
    public void OneMousePress(int button) {
        if (button == GLFW_MOUSE_BUTTON_1) {
            positions.add(MouseListener.getOrtho());
            isAtTargetPos = false;
//            System.out.println(String.format("Added %s,%s to movement queue. Current queue: %s", (int)MouseListener.getOrthoX(), (int)MouseListener.getOrthoY(), positions));

        }
    }

    @Override
    public void onMouseRelease(int button) {
        System.out.println("RELEASE " + button);
    }
}
