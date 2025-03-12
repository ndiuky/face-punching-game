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

    private static float scrollX;

    private static float scrollY;

    private static float xPos;

    private static float yPos;

    private static float lastY;

    private static float lastX;

    // TODO: 14.06.2024 опустить лимит кнопок до 3, нахуй кому-то больше нажимать?
    //TODO: возмножно удалить этот массив вообще, и ограничиться ивентами нажатия/отпускания
    private static final boolean[] mouseButtonPressed = new boolean[9];

    //TODO: убрать isDragging???
    private static boolean isDragging;

    private MouseListener() {
        scrollX = 0;
        scrollY = 0;
        xPos = 0;
        yPos = 0;
        lastX = 0;
        lastY = 0;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        lastX = (float) xPos;
        lastY = (float) yPos;
        MouseListener.xPos = (float) xPos;
        MouseListener.yPos = (float) yPos;
        isDragging = mouseButtonPressed[0] || mouseButtonPressed[1] || mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (button < mouseButtonPressed.length) {
                mouseButtonPressed[button] = true;

                List<MouseEventListener> listeners = pressListeners.get(button);
                if (listeners != null) {
                    for (MouseEventListener listener : listeners) {
                        listener.onMousePress(button);
                    }
                }
            }
        } else if (action == GLFW_RELEASE && button < mouseButtonPressed.length) {
            mouseButtonPressed[button] = false;
            isDragging = false;

            List<MouseEventListener> listeners = releaseListeners.get(button);
            if (listeners != null) {
                for (MouseEventListener listener : listeners) {
                    listener.onMouseRelease(button);
                }
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        scrollX = (float) xOffset;
        scrollY = (float) yOffset;
    }

    public static void reset() {
        scrollX = 0;
        scrollY = 0;
        lastX = xPos;
        lastY = yPos;
    }

    public static GameObject getGameObjectUnderCursor() {
        return Window.getCurrentScene().getGameObjectById(PickingTexture.readPixel((int) getX(), (int) getY()));
    }

    public static float getRelativeAngle(Vector2f position) {
        return (float) (Math.atan2(position.x - getOrthoX(), position.y - getOrthoY()) * 180 / Math.PI);
    }

    public static float getX() {
        return xPos;
    }

    public static float getY() {
        return yPos;
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
    //TODO: возможно объединить вычисление X и Y
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
        if (button < mouseButtonPressed.length) {
            return mouseButtonPressed[button];
        }

        return false;
    }

    public static float getDx() {
        return lastX - xPos;
    }

    public static float getDy() {
        return lastY - yPos;
    }

    public static float getScrollX() {
        return scrollX;
    }

    public static float getScrollY() {
        return scrollY;
    }

    public static boolean isDragging() {
        return isDragging;
    }
}