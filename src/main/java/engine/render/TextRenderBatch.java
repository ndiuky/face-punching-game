package engine.render;

import engine.component.CTextRenderer;
import engine.text.Font;
import engine.text.Glyph;
import engine.text.Text;
import engine.util.AssetPool;
import engine.util.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

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

public class TextRenderBatch implements Comparable<TextRenderBatch> {

    private static final int POS_SIZE = 2;

    private static final int COLOR_SIZE = 4;

    private static final int TEXTURE_COORDS_SIZE = 2;

    private static final int POS_OFFSET = 0;

    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private static final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    private final int maxBatchSize;

    private final int zIndex;

    private static final int VERTICES_PER_QUAD = 6;

    private static final int VERTEX_SIZE = 8;

    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private int vboID = -1;

    private final Shader shader = AssetPool.getShaderByName("fontShader");

    private final float[] vertices;

    private final CTextRenderer[] textRenderers;

    private final List<Text> contentToRender = new ArrayList<>();

    private int numVertices = 0;

    private int numElements = 0;

    private boolean hasRoom = true;

    private boolean started;

    public TextRenderBatch(int maxBatchSize, int zIndex) {
        this.zIndex = zIndex;
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * VERTICES_PER_QUAD * VERTEX_SIZE];
        textRenderers = new CTextRenderer[maxBatchSize];
    }

    public void start() {
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
//        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //TODO: мб можно перенести вызов enableVertexAttributes сюда, и вызывать его только один раз
    }

    public void beginFrame() {
        if (!started) {
            start();
            started = true;
        }


//        for (int i = 0; i < contentToRender.size(); i++) {
//            if (contentToRender.get(i).beginFrame() < 0) {
//                contentToRender.remove(i--).getTextSize();
//            }
//        }

//        glBindFragDataLocation(shader.getShaderProgramID(), 0, "fragColor");
    }

    public void render() {
        if (contentToRender.isEmpty()) {
            return;
        }

        glBindFragDataLocation(shader.getShaderProgramID(), 0, "fragColor");

        glActiveTexture(GL_TEXTURE0);
        Font.bind();

        boolean rebufferData = false;
        for (int i = 0; i < numElements; i++) {
            CTextRenderer textRenderer = textRenderers[i];
            if (textRenderer.getIsDirty()) {
                loadVertexProperties(i, textRenderer);
                textRenderer.setIsDirty();
                rebufferData = true;
            }
        }

//        if (rebufferData){
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, numVertices * Float.BYTES, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, numVertices));
//        }

        shader.use();
        enableVertexAttributes();

        shader.setUniform(shader.getUniformLocation("uTexture"), 0);

        //TODO: брать view матрицу из камеры
        shader.setUniform(shader.getUniformLocation("uView"),
                new Matrix4f()
//                Window.getCurrentScene().getCamera().getViewMatrix()
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

//        numVertices = 0;

        Font.unbind();

        shader.detach();
    }

    private void loadVertexProperties(int index, CTextRenderer textRenderer) {
        int offset = index * VERTICES_PER_QUAD * VERTEX_SIZE;
        Text text = textRenderer.getText();
        float xAdd = textRenderer.gameObject.transform.position.x;
        float yAdd = textRenderer.gameObject.transform.position.y;

        int textSize = text.getTextSize();
        Color color = text.getColor();
        List<Glyph> content = text.getContent();
        List<Vector2f> positions = text.getPositions();
        int width = Font.getTexture().getWidth();
        int height = Font.getTexture().getHeight();

        for (int i = 0; i < textSize; i++) {
            Vector2f pos = positions.get(i);
            Glyph glyph = content.get(i);

            boolean isRotated = textRenderer.gameObject.transform.rotation != 0;
            Matrix4f transformMatrix = new Matrix4f().identity();
            if (isRotated) {
                transformMatrix.translate(textRenderer.gameObject.transform.position.x,
                        textRenderer.gameObject.transform.position.y, 0f);
                transformMatrix.rotate((float) Math.toRadians(textRenderer.gameObject.transform.rotation),
                        0, 0, 1);
                transformMatrix.scale(textRenderer.gameObject.transform.scale.x,
                        textRenderer.gameObject.transform.scale.y, 1);
            }

            Vector4f currentPos = new Vector4f(
                    isRotated ?
                            new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix)
                            : new Vector4f(
                            textRenderer.gameObject.transform.position.x + textRenderer.gameObject.transform.scale.x,
                            textRenderer.gameObject.transform.position.y + textRenderer.gameObject.transform.scale.y,
                            0, 1)
            );

            float x1 = pos.x + xAdd;
            float y1 = pos.y + yAdd;
//                float x1 = currentPos.x + xAdd + pos.x;
//                float y1 = currentPos.y + yAdd + pos.y;
            float x2 = x1 + glyph.width;
            float y2 = y1 + glyph.height;

            float s1 = glyph.x / (float) width;
            float t1 = glyph.y / (float) height;
            float s2 = (glyph.x + glyph.width) / (float) width;
            float t2 = (glyph.y + glyph.height) / (float) height;

            float r = color.r;
            float g = color.g;
            float b = color.b;
            float a = color.a;

            System.out.println("x1 = " + x1);
            System.out.println("y1 = " + y1);
            System.out.println("x2 = " + x2);
            System.out.println("y2 = " + y2);
            System.out.println("s1 = " + s1);
            System.out.println("t1 = " + t1);
            System.out.println("s2 = " + s2);
            System.out.println("t2 = " + t2);

            //TODO: добавить ebo, в теории увеличит производительность рендера текста на ~30%
            //TODO: перетащить в fori

            vertices[offset] = x1;
            vertices[offset + 1] = y1;
            vertices[offset + 2] = r;
            vertices[offset + 3] = g;
            vertices[offset + 4] = b;
            vertices[offset + 5] = a;
            vertices[offset + 6] = s1;
            vertices[offset + 7] = t1;

            vertices[offset + 8] = x1;
            vertices[offset + 9] = y2;
            vertices[offset + 10] = r;
            vertices[offset + 11] = g;
            vertices[offset + 12] = b;
            vertices[offset + 13] = a;
            vertices[offset + 14] = s1;
            vertices[offset + 15] = t2;

            vertices[offset + 16] = x2;
            vertices[offset + 17] = y2;
            vertices[offset + 18] = r;
            vertices[offset + 19] = g;
            vertices[offset + 20] = b;
            vertices[offset + 21] = a;
            vertices[offset + 22] = s2;
            vertices[offset + 23] = t2;

            vertices[offset + 24] = x1;
            vertices[offset + 25] = y1;
            vertices[offset + 26] = r;
            vertices[offset + 27] = g;
            vertices[offset + 28] = b;
            vertices[offset + 29] = a;
            vertices[offset + 30] = s1;
            vertices[offset + 31] = t1;

            vertices[offset + 32] = x2;
            vertices[offset + 33] = y2;
            vertices[offset + 34] = r;
            vertices[offset + 35] = g;
            vertices[offset + 36] = b;
            vertices[offset + 37] = a;
            vertices[offset + 38] = s2;
            vertices[offset + 39] = t2;

            vertices[offset + 40] = x2;
            vertices[offset + 41] = y1;
            vertices[offset + 42] = r;
            vertices[offset + 43] = g;
            vertices[offset + 44] = b;
            vertices[offset + 45] = a;
            vertices[offset + 46] = s2;
            vertices[offset + 47] = t1;

            offset += VERTICES_PER_QUAD * VERTEX_SIZE;
        }
    }

    public void addElement(CTextRenderer textRenderer) {
        textRenderers[numElements] = textRenderer;

        if (textRenderer.getText() != null) {
            if (!hasText(textRenderer.getText())) {
                contentToRender.add(textRenderer.getText());
            }
        }
        numVertices += textRenderer.getText().getTextSize() * VERTEX_SIZE * VERTICES_PER_QUAD;

        loadVertexProperties(numElements++, textRenderer);

        if (numElements >= maxBatchSize) {
            hasRoom = false;
        }
    }

    //TODO: ВРЕМЕННЫЙ МЕТОД, ПЕРЕДЕЛАТЬ ПОД УДАЛЕНИЕ ПО ID ТЕКСТА!!!
    public void removeText(String textToDelete) {
        for (int i = 0; i < contentToRender.size(); i++) {
            if (contentToRender.get(i).getTextContent().equals(textToDelete)) {
                contentToRender.remove(i);

                return;
            }
        }
    }

    private void enableVertexAttributes() {
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);

        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDS_OFFSET);
    }

    private void disableVertexAttributes() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
    }

    public boolean hasRoom() {
        return hasRoom;
    }

    public int getZIndex() {
        return zIndex;
    }

    public boolean hasTextRoom() {
        return contentToRender.size() < 8;
    }

    public boolean hasText(Text text) {
        return contentToRender.contains(text);
    }

    @Override
    public int compareTo(TextRenderBatch o) {
        return Integer.compare(zIndex, o.getZIndex());
    }
}
