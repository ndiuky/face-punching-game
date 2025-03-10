package engine.util.listener;

import engine.event.MouseEventListener;
import engine.event.MouseEventProducer;
import engine.object.GameObject;
import engine.render.Camera;
import engine.render.PickingTexture;
import engine.render.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener implements MouseEventProducer {

    // TODO: 23.06.2024 переделать переменные на static чтобы освободить стекфреймы, создаваемые при вызове геттеров
    private static MouseListener instance;

    private double scrollX;

    private double scrollY;

    private double xPos;

    private double yPos;

    private double lastY;

    private double lastX;

    // TODO: 14.06.2024 опустить лимит кнопок до 3, нахуй кому-то больше нажимать?
    private final boolean[] mouseButtonPressed = new boolean[9];

    private boolean isDragging;

    private MouseListener() {
        scrollX = 0;
        scrollY = 0;
        xPos = 0;
        yPos = 0;
        lastX = 0;
        lastY = 0;
    }

    public static MouseListener get() {
        if (instance == null) {
            instance = new MouseListener();
        }

        return instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;

                final List<MouseEventListener> listeners = pressListeners.get(button);
                if (listeners != null) {
                    listeners.forEach(listener -> listener.onMousePress(button));
                }
            }
        } else if (action == GLFW_RELEASE && button < get().mouseButtonPressed.length) {
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;

            final List<MouseEventListener> listeners = releaseListeners.get(button);
            if (listeners != null) {
                listeners.forEach(listener -> listener.onMouseRelease(button));
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void reset() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static GameObject getGameObjectUnderCursor() {
//        System.out.println(getX() + " " + getY());
//        System.out.println(getOrthoX() + " " + getOrthoY());
        return Window.getCurrentScene().getGameObjectById(PickingTexture.readPixel((int) getX(), (int) getY()));
    }

    public static float getRelativeAngle(Vector2f position) {
        return (float) (Math.atan2(position.x - getOrthoX(), position.y - getOrthoY()) * 180 / Math.PI);
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static Vector2f getPos() {
        return new Vector2f(getX(), getY());
    }

    public static float getScreenX() {
        return getX() * Window.getWidth();
    }

    public static float getScreenY() {
        return getY() * Window.getHeight();
    }

    // TODO: 13.07.2024 добавить кеширование вычисленного значение для каждого кадра, и доставать его при запросе в том же кадре. может повысить производительность, если количество запросов за кадр слишком большое. Возможно стоит применять эту технику только при большом объёме вызовов, в ином случае вычислять заново
    //TODO: убрать лишние переменные
    public static float getOrthoX() {
        float currentX = getX();
        currentX = (currentX / (float) Window.getWidth()) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);

        Camera camera = Window.getCurrentScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);
        currentX = tmp.x;

        return currentX;
    }

    public static float getOrthoY() {
        float currentY = getY();
        currentY = -((currentY / (float) Window.getHeight()) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);

        Camera camera = Window.getCurrentScene().getCamera();
        Matrix4f viewProjection = new Matrix4f();
        camera.getInverseViewMatrix().mul(camera.getInverseProjectionMatrix(), viewProjection);
        tmp.mul(viewProjection);
        currentY = tmp.y;

        return currentY;
    }

    public static Vector2f getOrtho() {
        return new Vector2f(getOrthoX(), getOrthoY());
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        }

        return false;
    }

    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float) (get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }
}