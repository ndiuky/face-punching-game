package engine.render;

import engine.text.Font;
import engine.text.Glyph;
import engine.text.Text;
import engine.util.AssetPool;
import engine.util.Color;
import engine.util.EnginePreferences;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL20C.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindFragDataLocation;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

/**
 * This class is performing the rendering process.
 *
 * @author Heiko Brumme
 */
public class TextRenderer {

    private static int vaoID = -1;
    private static int vboID = -1;
    private static final Shader shader = AssetPool.getShader("asset/shader/fontShader.glsl");

    private static FloatBuffer vertices;
    private static int numVertices;
    private static boolean drawing;

    private static String text;
    private static float posX, posY;

    public void update(float dt) {
        drawText(text, posX, posY);
    }

    public TextRenderer setText(String text) {
        TextRenderer.text = text;
        return this;
    }

    public TextRenderer setPos(float x, float y) {
        posX = x;
        posY = y;

        return this;
    }

    public static void start() {
        initialize(EnginePreferences.FONT_RESOLUTION);
    }

    /**
     * Initializes the renderer.
     */

    public static void initialize(int fontSize) {

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        /* Enable blending */
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Clears the drawing area.
     */
    public static void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Begin rendering.
     */
    public static void begin() {
        if (drawing) {
            throw new IllegalStateException("Renderer is already drawing!");
        }
        setupShaderProgram();
        drawing = true;
        numVertices = 0;
    }

    /**
     * End rendering.
     */
    public static void end() {
        if (!drawing) {
            throw new IllegalStateException("Renderer isn't drawing!");
        }
        drawing = false;
        flush();
    }


    public static void flush() {
        if (numVertices > 0) {
            vertices.flip();

//            if (vaoID != -1) {
//                vaoID = glGenVertexArrays();
//            }

            glBindBuffer(vboID, GL_ARRAY_BUFFER);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

            shader.use();
            enableVertexAttributes();

            long window = GLFW.glfwGetCurrentContext();
            int width, height;
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);
                GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
                width = widthBuffer.get();
                height = heightBuffer.get();
            }

            shader.setUniform(shader.getUniformLocation("texImage"), 0);

            shader.setUniform(shader.getUniformLocation("model"),
                    new Matrix4f()
            );

            shader.setUniform(shader.getUniformLocation("view"),
                    new Matrix4f()
            );

            shader.setUniform(shader.getUniformLocation("projection"),
//                    Window.getCurrentScene().getCamera().getProjectionMatrix()
                    new Matrix4f(
                            2f / width, 0f, 0f, -1f,
                            0f, 2f / height, 0f, -1f,
                            0f, 0f, -1f, 0f,
                            0f, 0f, 0f, 1f
                    )
            );

            glDrawArrays(GL_TRIANGLES, 0, numVertices);

            disableVertexAttributes();
            glBindBuffer(0, GL_ARRAY_BUFFER);
            glBindVertexArray(0);

            vertices.clear();
            numVertices = 0;

            //TODO: если использовать detach(), то текст не будет рендериться
            shader.detach();
        }
    }

//    public int getTextWidth(CharSequence text) {
//        return font.getWidth(text);
//    }
//
//    public int getTextHeight(CharSequence text) {
//        return font.getHeight(text);
//    }

    //TODO: THIS
    public static void drawText(CharSequence text, float x, float y) {
        Font.drawText(text, x, y);
    }

    public static void drawTextureRegion(Texture texture, float x, float y, float regX, float regY, float regWidth, float regHeight, Color c) {
        /* Vertex positions */
        float x1 = x;
        float y1 = y;
        float x2 = x + regWidth;
        float y2 = y + regHeight;

        //TODO: заранее вычислить все текстурные координаты
        float s1 = regX / texture.getWidth();
        float t1 = regY / texture.getHeight();
        float s2 = (regX + regWidth) / texture.getWidth();
        float t2 = (regY + regHeight) / texture.getHeight();

        drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
    }

    public static void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, Color c) {
        if (vertices.remaining() < 8 * 6) {
            /* We need more space in the buffer, so flush it */
            flush();
        }

        float r = c.r;
        float g = c.g;
        float b = c.b;
        float a = c.a;

        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
        vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2);
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);

        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
        vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1);

        numVertices += 6;
    }

    public static void drawFullText(Text text) {
        int textSize = text.getTextSize();
        Color color = text.getColor();
        List<Glyph> content = text.getContent();
        List<Vector2f> positions = text.getPositions();

        for (int i = 0; i < textSize; i++) {
            Vector2f pos = positions.get(i);
            Glyph glyph = content.get(i);
            drawTextureRegion(Font.getTexture(), pos.x, pos.y, glyph.x, glyph.y, glyph.width, glyph.height, color);
        }
    }

    /**
     * Dispose renderer and clean up its used data.
     */
    public void dispose() {
        MemoryUtil.memFree(vertices);

        if (vaoID != -1) {
            glDeleteVertexArrays(vaoID);
        }
        glDeleteBuffers(vboID);
        shader.detach();

        Font.dispose();
    }

    /**
     * Setups the default shader shader.
     */
    public static void setupShaderProgram() {

        /* Create FloatBuffer */
        vertices = MemoryUtil.memAllocFloat(4096);

        /* Upload null data to allocate storage for the VBO */
        long size = vertices.capacity() * Float.BYTES;
        glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

        /* Initialize variables */
        numVertices = 0;
        drawing = false;

        glBindFragDataLocation(shader.getShaderProgramID(), 0, "fragColor");
    }

    /**
     * Specifies the vertex pointers.
     */
//    private void specifyVertexAttributes(boolean enable) {
    private static void enableVertexAttributes() {

        int posAttrib = shader.getAttributeLocation("position");
        int colAttrib = shader.getAttributeLocation("color");
        int texAttrib = shader.getAttributeLocation("texcoord");

        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 0);

        glEnableVertexAttribArray(colAttrib);
        glVertexAttribPointer(colAttrib, 4, GL_FLOAT, false, 8 * Float.BYTES, 2 * Float.BYTES);

        glEnableVertexAttribArray(texAttrib);
        glVertexAttribPointer(texAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
    }

    private static void disableVertexAttributes() {

        int posAttrib = shader.getAttributeLocation("position");
        int colAttrib = shader.getAttributeLocation("color");
        int texAttrib = shader.getAttributeLocation("texcoord");

        glDisableVertexAttribArray(posAttrib);
//        glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 0);

        glDisableVertexAttribArray(colAttrib);
//        glVertexAttribPointer(colAttrib, 4, GL_FLOAT, false, 8 * Float.BYTES, 2 * Float.BYTES);

        glDisableVertexAttribArray(texAttrib);
//        glVertexAttribPointer(texAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
    }

}
