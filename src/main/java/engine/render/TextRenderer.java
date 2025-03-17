package engine.render;

import engine.text.Font;
import engine.text.Glyph;
import engine.text.Text;
import engine.util.AssetPool;
import engine.util.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;

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

public class TextRenderer {

    private static final int MAX_GLYPHS = 1024;

    private static final int VERTICES_PER_QUAD = 6;

    private static final int VERTEX_SIZE = 8;

    private static int vboID = -1;
    private static final Shader shader = AssetPool.getShaderByName("fontShader");

    private static final float[] vertices = new float[MAX_GLYPHS * VERTICES_PER_QUAD * VERTEX_SIZE];
    private static final List<Text> contentToRender = new ArrayList<>();
    private static int numVertices = 0;

    private static boolean started;

    private static void start() {
        int vaoID = glGenVertexArrays();

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
                contentToRender.remove(i--).getTextSize();
            }
        }

        glBindFragDataLocation(shader.getShaderProgramID(), 0, "fragColor");
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
    }

    public static void draw() {
        if (contentToRender.isEmpty()) {
            return;
        }

        glActiveTexture(GL_TEXTURE0);
        Font.bind();

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

                float x1 = pos.x;
                float y1 = pos.y;
                float x2 = x1 + glyph.width;
                float y2 = y1 + glyph.height;

                float s1 = glyph.x / (float) width;
                float t1 = glyph.y / (float) height;
                float s2 = (glyph.x + glyph.width) / (float) width;
                float t2 = (glyph.y + glyph.height) / (float) height;

                //TODO: вернуть логику предварительного рендера батча
//                if (vertices.remaining() < VERTEX_SIZE * VERTICES_PER_QUAD) {
//                    draw();
//                }

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

                numVertices += VERTICES_PER_QUAD * VERTEX_SIZE;
            }
        }


        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, numVertices));

        shader.use();
        enableVertexAttributes();

        shader.setUniform(shader.getUniformLocation("uTexture"), 0);

        shader.setUniform(shader.getUniformLocation("uModel"),
                new Matrix4f()
        );

        //TODO: брать view матрицу из камеры
        shader.setUniform(shader.getUniformLocation("uView"),
                new Matrix4f()
        );

        //TODO: брать матрицу проекции из камеры
        shader.setUniform(shader.getUniformLocation("uProjection"),
//                    Window.getCurrentScene().getCamera().getProjectionMatrix()
                new Matrix4f(
                        2f / Window.getWidth(), 0f, 0f, -1f,
                        0f, 2f / Window.getHeight(), 0f, -1f,
                        0f, 0f, -1f, 0f,
                        0f, 0f, 0f, 1f
                )
        );

        glDrawArrays(GL_TRIANGLES, 0, numVertices);

        disableVertexAttributes();
        glBindBuffer(0, GL_ARRAY_BUFFER);
        glBindVertexArray(0);

        numVertices = 0;

        //TODO: возможно здесь нужно вызывать detach() на текстуре
//        Font.unbind();

        shader.detach();
    }

    public static void add(String text, float x, float y) {
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

    public static void drawFullText(Text text) {
        contentToRender.add(text);
    }

    private static void enableVertexAttributes() {

        //TODO: переделать на константные значения
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

        //TODO: переделать на константные значения
        int posAttrib = shader.getAttributeLocation("position");
        int colAttrib = shader.getAttributeLocation("color");
        int texAttrib = shader.getAttributeLocation("texcoord");

        glDisableVertexAttribArray(posAttrib);

        glDisableVertexAttribArray(colAttrib);

        glDisableVertexAttribArray(texAttrib);
    }

}
