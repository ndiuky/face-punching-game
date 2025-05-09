package engine.render;

import engine.util.AssetPool;
import engine.util.Color;
import engine.util.EnginePreferences;
import org.joml.Vector2f;
import org.joml.Vector4f;
import registry.ShaderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static engine.util.EngineUtils.rotateVertex;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_LINES;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDrawArrays;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15.glLineWidth;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class DebugDraw {
    private static final int MAX_LINES = 4096;

    private static final int INDICES_COUNT = 6;

    private static final List<Line> lines = new ArrayList<>(MAX_LINES);

    private static final float[] vertices = new float[MAX_LINES * INDICES_COUNT * 2];

    private static final Shader shader = AssetPool.getShader(ShaderType.DEBUG_LINE);

    private static int vaoID;

    private static int vboID;

    private static boolean started;

    public static void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, INDICES_COUNT * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, INDICES_COUNT * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(1.0f);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        lines.clear();
    }


    public static void draw() {
        if (lines.isEmpty()) {
            return;
        }

        int index = 0;
        for (Line line : lines) {
            Vector4f color = line.getColor();

            for (int i = 0; i < 2; i++) {
                Vector2f position = i == 0 ? line.getFrom() : line.getTo();

                vertices[index] = position.x;
                vertices[index + 1] = position.y;
                vertices[index + 2] = line.getzOrder();

                vertices[index + 3] = color.x;
                vertices[index + 4] = color.y;
                vertices[index + 5] = color.z;
                index += INDICES_COUNT;
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, lines.size() * INDICES_COUNT * 2));

        shader.use();
        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, lines.size() * INDICES_COUNT * 2);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    public static void drawLine(Vector2f from, Vector2f to, int zOrder) {
        drawLine(from, to, zOrder, Color.GREEN);
    }

    public static void drawLine(Vector2f from, Vector2f to, int zOrder, Color color) {
        if (lines.size() >= MAX_LINES) {
            return;
        }

        lines.add(new Line(from, to, zOrder, color.asVector()));
    }

    public static void drawBox(Vector2f center, Vector2f size, int zOrder, float rotation) {
        drawBox(center, size, zOrder, rotation, Color.BLUE, 1);
    }

    public static void drawBox(Vector2f center, Vector2f size, int zOrder, float rotation, Color color) {
        drawBox(center, size, zOrder, rotation, color, 1);
    }

    public static void drawBox(Vector2f center, Vector2f size, int zOrder, float rotation,
                               Color color, int lifetime) {
        Vector2f min = new Vector2f(center).sub(new Vector2f(size).mul(0.5f));
        Vector2f max = new Vector2f(center).add(new Vector2f(size).mul(0.5f));

        Vector2f[] vertices = {
                new Vector2f(min.x, min.y), new Vector2f(min.x, max.y),
                new Vector2f(max.x, max.y), new Vector2f(max.x, min.y)
        };

        if (rotation != 0.0f) {
            for (Vector2f vertex : vertices) {
                rotateVertex(vertex, rotation, center);
            }
        }

        drawLine(vertices[0], vertices[1], zOrder, color);
        drawLine(vertices[1], vertices[2], zOrder, color);
        drawLine(vertices[2], vertices[3], zOrder, color);
        drawLine(vertices[3], vertices[0], zOrder, color);
    }

    public static void drawCircle(Vector2f center, float radius, int zOrder) {
        drawCircle(center, radius, zOrder, Color.BLUE, 1, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID);
    }

    public static void drawCircle(Vector2f center, float radius, int zOrder, Color color) {
        drawCircle(center, radius, zOrder, color, 1, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID);
    }

    public static void drawCircle(Vector2f center, float radius, int zOrder, Color color, int lifetime) {
        drawCircle(center, radius, zOrder, color, lifetime, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID);
    }

    public static void drawCircle(Vector2f center, float radius, int zOrder, Color color, int lifetime, int segments) {
        Vector2f[] points = new Vector2f[segments];
        float increment = 360f / points.length;
        float currentAngle = 0;

        for (int i = 0; i < points.length; i++) {
            Vector2f tmp = new Vector2f(0, radius);
            rotateVertex(tmp, currentAngle, new Vector2f());
            points[i] = new Vector2f(tmp).add(center);

            if (i > 0) {
                drawLine(points[i - 1], points[i], zOrder, color);
            }
            currentAngle += increment;
        }

        drawLine(points[points.length - 1], points[0], zOrder, color);
    }

    public static void drawPath(Vector2f[] path) {
        for (int i = 0; i < path.length - 1; i++) {
            drawLine(path[i], path[i + 1], -1, Color.GREEN);
        }
    }

    public static void drawPath(Vector2f[] path, int lifetime) {
        for (int i = 0; i < path.length - 1; i++) {
            drawLine(path[i], path[i + 1], -1, Color.GREEN);
        }
    }
}