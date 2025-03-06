package engine.util;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Utils {

    public static void rotateVertex(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        vec.x = (x * cos) - (y * sin) + origin.x;
        vec.y = (x * sin) + (y * cos) + origin.y;
    }

    public static float time(){
        return (float) glfwGetTime();
    }
}