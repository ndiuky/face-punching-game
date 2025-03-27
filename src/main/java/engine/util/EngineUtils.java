package engine.util;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class EngineUtils {

    public static void rotateVertex(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        vec.x = (x * cos) - (y * sin) + origin.x;
        vec.y = (x * sin) + (y * cos) + origin.y;
    }

    /**
     * Эквивалент оригинального метода, но еще и вычисляет дистанцию, а не принимает её извне.
     * Нужен в случае, если дистанция ранее не вычислялась
     */
    public static Vector2f calculateClampedPosition(Vector2f from, Vector2f to, float maxLength) {
        float distance = from.distance(to);

        return calculateClampedPosition(from, to, maxLength, distance);
    }

    public static Vector2f calculateClampedPosition(Vector2f from, Vector2f to, float maxLength, float distance) {
        if (distance <= maxLength) {
            return new Vector2f(to);
        }

        float t = maxLength / distance;

        float newX = from.x + t * (to.x - from.x);
        float newY = from.y + t * (to.y - from.y);

        return new Vector2f(newX, newY);
    }

    //TODO: добавь точно такой же метод, но с from=0 по умолчанию, ибо чаще всего такой он и приходит
    public static float inverseLerpUnclamped(float from, float to, float midPoint) {
        return from + midPoint / (to - from);
    }

    // TODO: эта штука много где дёргается, стоит кешировать каждый кадр. Возможно также стоит кешировать синус времени, ибо его тоже часто дёргают
    public static float time() {
        return (float) glfwGetTime();
    }
}