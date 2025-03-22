package engine.render;

import engine.component.CTextRenderer;
import engine.text.Font;
import engine.text.Glyph;
import engine.text.Text;
import engine.util.AssetPool;
import engine.util.Color;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static engine.util.EngineUtils.time;
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

    private int vaoID = -1;

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
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
//        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        enableVertexAttributes();
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

        //TODO: вернуть функционал ребафферинга
//        if (rebufferData){
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, numVertices * Float.BYTES, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, numVertices));
//        }

        shader.use();

        shader.setUniform(shader.getUniformLocation("uTexture"), 0);


        Matrix4f matrix = Window.getCurrentScene().getCamera().getProjectionMatrix();
        shader.setUniform(shader.getUniformLocation("uProjection"),
                new Matrix4f(matrix)
                        .m30(0)
                        .m31(0)
//                        .m03(Window.getWidth() * 0.01f * ((float) Math.sin(time())))
//                        .m13(Window.getHeight() * 0.01f * ((float) Math.sin(time())))

                        .m03(-1)
                        .m13(-1)

//                new Matrix4f(
//                        2f / Window.getWidth(), 0f, 0f, 0f,
//                        0f, 2f / Window.getHeight(), 0f, 0f,
//                        0f, 0f, -1f, 0f,
//                        0f, 0f, 0f, 1f
//                )
        );
        Matrix4f view = Window.getCurrentScene().getCamera().getViewMatrix();
        shader.setUniform(shader.getUniformLocation("uView"),
//                new Matrix4f(Window.getCurrentScene().getCamera().getViewMatrix())
                new Matrix4f()
                        .m03(view.m30())
                        .m13(view.m31())
        );

        System.out.println(
                new Matrix4f().m00(00).m01(01).m02(02).m03(03).m10(10).m11(11).m12(12).m13(13).m20(20).m21(21).m22(22).m23(23).m30(30).m31(31).m32(32).m33(33) + "\n" +
//                        new Matrix4f(Window.getCurrentScene().getCamera().getProjectionMatrix()) + "\n" +
                        new Matrix4f(Window.getCurrentScene().getCamera().getProjectionMatrix())
//                                .m30(0)
//                                .m31(0)
//                                .m03(-1)
//                                .m13(-1)
                        + "\n" +
                        new Matrix4f(
                                2f / Window.getWidth(), 0f, 0f, -1f,
                                0f, 2f / Window.getHeight(), 0f, -1f,
                                0f, 0f, -1f, 0f,
                                0f, 0f, 0f, 1f
                        )
        );

        //TODO: скорее всего это не нужно
        glBindVertexArray(vaoID);
        enableVertexAttributes();

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
//                transformMatrix.scale(textRenderer.gameObject.transform.scale.x,
//                        textRenderer.gameObject.transform.scale.y, 1);
            }

            Vector4f currentPos = new Vector4f(
                    isRotated ?
                            new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix)
                            : new Vector4f(
                            xAdd,// + textRenderer.gameObject.transform.scale.x,
                            yAdd,// + textRenderer.gameObject.transform.scale.y,
                            0, 1)
            );

            float x1 = currentPos.x + pos.x;
            float y1 = currentPos.y + pos.y;
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
