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

    public static Vector2f calculateClampedPosition(Vector2f from, Vector2f to, float maxLength) {
        float distance = from.distance(to);

        if (distance <= maxLength) {
            return new Vector2f(to);
        }

        float t = maxLength / distance;

        float newX = from.x + t * (to.x - from.x);
        float newY = from.y + t * (to.y - from.y);

        return new Vector2f(newX, newY);
    }

    /**
     * Эквивалент оригинального метода, но не вычисляет дистанцию, а принимает её извне.
     * Нужен для сохранения производительности за счёт отсутствия повторного вычисления дистанции
     */
    public static Vector2f calculateClampedPosition(Vector2f from, Vector2f to, float maxLength, float distance) {
        if (distance <= maxLength) {
            return new Vector2f(to);
        }

        float t = maxLength / distance;

        float newX = from.x + t * (to.x - from.x);
        float newY = from.y + t * (to.y - from.y);

        return new Vector2f(newX, newY);
    }

    public static float time() {
        return (float) glfwGetTime();
    }
}