package сomponent.entity;

import engine.util.listener.InputEventListener;
import engine.util.listener.MouseListener;
import gameComponent.GameComponent;
import org.joml.Vector2f;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class Player extends Entity implements InputEventListener{

    private float xDir, yDir;

    private float movementSpeed = 400;

    private transient Vector2f targetPos = new Vector2f();

    private transient boolean isAtTargetPos = true;

//    private transient PriorityQueue<Action> actions;

    //TODO: заменить очередь векторов на очередь Actions
    private transient Queue<Vector2f> positions = new ArrayDeque<>();

    private Vector2f lastPos = new Vector2f(0,0);

    @Override
    public void start() {
        MouseListener.listenToPress(GLFW_MOUSE_BUTTON_1, this);
        MouseListener.listenToRelease(GLFW_MOUSE_BUTTON_2, this);

        gameComponents.forEach(gc -> gc.init(this));
    }

    @Override
    public void update(final float dt) {

        for (GameComponent gameComponent : gameComponents) {
            if (gameComponent.getIsPassive()){
                gameComponent.execute();
            }
        }


//        if (!isAtTargetPos) {
//            goToTarget(dt);
//        }

    }

    private void executeNextAction(){
        //метод для выполнения следующего действия в очереди
    }

    @Override
    public void OneMousePress(int button) {
        if (button == GLFW_MOUSE_BUTTON_1) {
            positions.add(MouseListener.getOrtho());
            isAtTargetPos = false;
            System.out.println(String.format("Added %s,%s to movement queue. Current queue: %s", (int)MouseListener.getOrthoX(), (int)MouseListener.getOrthoY(), positions));

            if (MouseListener.getGameObjectUnderCursor() != null) {
                System.out.println(MouseListener.getGameObjectUnderCursor().getName());
            }
        }
    }

    @Override
    public void onMouseRelease(int button) {
        System.out.println("RELEASE " + button);
    }
}
