package engine.render;

import engine.text.Font;
import engine.text.Glyph;
import engine.text.Text;
import engine.util.AssetPool;
import engine.util.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
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

    private static final int MAX_TEXT_BLOCKS = 4096;

    private static final int VERTICES_PER_QUAD = 6;

    private static final int VERTEX_SIZE = 8;

    private static int vaoID = -1;
    private static int vboID = -1;
    private static final Shader shader = AssetPool.getShader("asset/shader/fontShader.glsl");

    //    private static FloatBuffer vertices;
    private static final float[] vertices = new float[MAX_TEXT_BLOCKS * VERTICES_PER_QUAD * 2];
    private static final List<Text> contentToRender = new ArrayList<>(MAX_TEXT_BLOCKS);
    private static int numVertices = 0;
    private static int numElements = 0;

    private static boolean started;

    private static void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void beginFrame() {
        if (!started) {
            start();
            started = true;
        }

        for (int i = 0; i < contentToRender.size(); i++) {
            if (contentToRender.get(i).beginFrame() < 0) {
                numElements -= contentToRender.remove(i--).getTextSize();
            }
        }

        glBindFragDataLocation(shader.getShaderProgramID(), 0, "fragColor");
//        setupShaderProgram();
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
    }

    public static void draw() {
        if (contentToRender.isEmpty()) {
            return;
        }

        glActiveTexture(GL_TEXTURE0);
        Font.getTexture().bind();

        for (Text text : contentToRender) {
            int textSize = text.getTextSize();
            Color color = text.getColor();
            List<Glyph> content = text.getContent();
            List<Vector2f> positions = text.getPositions();
            int width = Font.getTexture().getWidth();
            int height = Font.getTexture().getHeight();

            for (int i = 0; i < textSize; i++) {
                Vector2f pos = positions.get(i);
                Glyph glyph = content.get(i);

//                    drawTextureRegion(Font.getTexture(), pos.x, pos.y, glyph.x, glyph.y, glyph.width, glyph.height, color);

                float x1 = pos.x;
                float y1 = pos.y;
                float x2 = x1 + glyph.width;
                float y2 = y1 + glyph.height;

                float s1 = glyph.x / (float) width;
                float t1 = glyph.y / (float) height;
                float s2 = (glyph.x + glyph.width) / (float) width;
                float t2 = (glyph.y + glyph.height) / (float) height;

//                    drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);

                //TODO: вернуть логику предварительного рендера батча
//                    if (vertices.remaining() < VERTEX_SIZE * VERTICES_PER_QUAD) {
//                        /* We need more space in the buffer, so flush it */
//                        draw();
//                    }

                float r = color.r;
                float g = color.g;
                float b = color.b;
                float a = color.a;


                //TODO: добавить ebo, в теории увеличит производительность рендера текста на ~30%
                //TODO: перетащить в fori

                vertices[numVertices] = x1;
                vertices[numVertices + 1] = y1;
                vertices[numVertices + 2] = r;
                vertices[numVertices + 3] = g;
                vertices[numVertices + 4] = b;
                vertices[numVertices + 5] = a;
                vertices[numVertices + 6] = s1;
                vertices[numVertices + 7] = t1;

                vertices[numVertices + 8] = x1;
                vertices[numVertices + 9] = y2;
                vertices[numVertices + 10] = r;
                vertices[numVertices + 11] = g;
                vertices[numVertices + 12] = b;
                vertices[numVertices + 13] = a;
                vertices[numVertices + 14] = s1;
                vertices[numVertices + 15] = t2;

                vertices[numVertices + 16] = x2;
                vertices[numVertices + 17] = y2;
                vertices[numVertices + 18] = r;
                vertices[numVertices + 19] = g;
                vertices[numVertices + 20] = b;
                vertices[numVertices + 21] = a;
                vertices[numVertices + 22] = s2;
                vertices[numVertices + 23] = t2;

                vertices[numVertices + 24] = x1;
                vertices[numVertices + 25] = y1;
                vertices[numVertices + 26] = r;
                vertices[numVertices + 27] = g;
                vertices[numVertices + 28] = b;
                vertices[numVertices + 29] = a;
                vertices[numVertices + 30] = s1;
                vertices[numVertices + 31] = t1;

                vertices[numVertices + 32] = x2;
                vertices[numVertices + 33] = y2;
                vertices[numVertices + 34] = r;
                vertices[numVertices + 35] = g;
                vertices[numVertices + 36] = b;
                vertices[numVertices + 37] = a;
                vertices[numVertices + 38] = s2;
                vertices[numVertices + 39] = t2;

                vertices[numVertices + 40] = x2;
                vertices[numVertices + 41] = y1;
                vertices[numVertices + 42] = r;
                vertices[numVertices + 43] = g;
                vertices[numVertices + 44] = b;
                vertices[numVertices + 45] = a;
                vertices[numVertices + 46] = s2;
                vertices[numVertices + 47] = t1;

//                    offset += VERTEX_SIZE * VERTICES_PER_QUAD;
                numVertices += VERTICES_PER_QUAD * VERTEX_SIZE;

//                    vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
//                    vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2);
//                    vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
//
//                    vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
//                    vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
//                    vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1);
//
//                    numVertices += 6;
            }
        }


        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        float[] data = Arrays.copyOfRange(vertices, 0, numVertices);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);

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

        numVertices = 0;

        //TODO: если использовать detach(), то текст не будет рендериться
        shader.detach();

    }

    public static void drawText(String text, float x, float y) {
        Font.drawText(text, x, y);
    }

    //TODO: ВРЕМЕННЫЙ МЕТОД, ПЕРЕДЕЛАТЬ ПОД УДАЛЕНИЕ ПО ID ТЕКСТА!!!
    public static void removeText(String textToDelete) {
        for (int i = 0; i < contentToRender.size(); i++) {
            if (contentToRender.get(i).getTextContent().equals(textToDelete)) {
                contentToRender.remove(i);

                return;
            }
        }
    }

    public static void drawTextureRegion(Texture texture, float x, float y, float regX, float regY, float regWidth, float regHeight, Color c) {
//        /* Vertex positions */
//        float x1 = x;
//        float y1 = y;
//        float x2 = x + regWidth;
//        float y2 = y + regHeight;
//
//        //TODO: заранее вычислить все текстурные координаты
//        float s1 = regX / texture.getWidth();
//        float t1 = regY / texture.getHeight();
//        float s2 = (regX + regWidth) / texture.getWidth();
//        float t2 = (regY + regHeight) / texture.getHeight();
//
//        drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
    }

    public static void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, Color c) {
//        if (vertices.remaining() < 8 * 6) {
//            /* We need more space in the buffer, so flush it */
//            draw();
//        }
//
//        float r = c.r;
//        float g = c.g;
//        float b = c.b;
//        float a = c.a;
//
//        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
//        vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2);
//        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
//
//        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
//        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
//        vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1);
//
//        numVertices += 6;
    }

    public static void drawFullText(Text text) {
        contentToRender.add(text);
        numElements += text.getTextSize();

//        int textSize = text.getTextSize();
//        Color color = text.getColor();
//        List<Glyph> content = text.getContent();
//        List<Vector2f> positions = text.getPositions();
//
//        for (int i = 0; i < textSize; i++) {
//            Vector2f pos = positions.get(i);
//            Glyph glyph = content.get(i);
//            drawTextureRegion(Font.getTexture(), pos.x, pos.y, glyph.x, glyph.y, glyph.width, glyph.height, color);
//        }
    }

    /**
     * Dispose renderer and clean up its used data.
     */
//    public void dispose() {
//        MemoryUtil.memFree(vertices);
//
//        if (vaoID != -1) {
//            glDeleteVertexArrays(vaoID);
//        }
//        glDeleteBuffers(vboID);
//        shader.detach();
//
//        Font.dispose();
//    }

    /**
     * Setups the default shader shader.
     */
//    public static void setupShaderProgram() {
//
//        /* Create FloatBuffer */
//        vertices = MemoryUtil.memAllocFloat(4096);
//
//        /* Upload null data to allocate storage for the VBO */
//        long size = vertices.capacity() * Float.BYTES;
//        glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
//
//        /* Initialize variables */
//        numVertices = 0;
//        drawing = false;
//
//        glBindFragDataLocation(shader.getShaderProgramID(), 0, "fragColor");
//    }

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
