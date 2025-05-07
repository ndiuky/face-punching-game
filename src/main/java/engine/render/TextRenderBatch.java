package engine.render;

import engine.component.ui.CTextRenderer;
import engine.object.GameObject;
import engine.text.Font;
import engine.text.Glyph;
import engine.text.Text;
import engine.util.AssetPool;
import engine.util.Color;
import engine.util.EngineUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static engine.util.EnginePreferences.DEFAULT_QUAD_VERTICES_AMOUNT;
import static engine.util.EnginePreferences.EBO_QUAD_VERTICES_AMOUNT;
import static engine.util.EnginePreferences.TEXT_BASE_SIZE;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
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

// TODO: рендер батчей как бы есть, но логики их создания по сути нет. Если одновременно попытаться отрендерить слишком много текста, то просто вылетит эксепшен
public class TextRenderBatch implements Comparable<TextRenderBatch> {

    private static final int POS_SIZE = 2;

    private static final int COLOR_SIZE = 4;

    private static final int TEXTURE_COORDS_SIZE = 2;

    private static final int POS_OFFSET = 0;

    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private static final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    private final int maxBatchSize;

    private final int zIndex;

    private static final int VERTEX_SIZE = 8;

    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private int vboID = -1;

    private int vaoID = -1;

    private final Shader shader = AssetPool.getShader("fontShader");

    private final float[] vertices;

    private final CTextRenderer[] textRenderers;

    private final List<Text> contentToRender = new ArrayList<>();

    private int numVertices;

    private int numTextBlocks;

    private int numGlyphs = 0;

    private boolean hasRoom = true;

    public TextRenderBatch(int maxBatchSize, int zIndex) {
        this.zIndex = zIndex;
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE];
        //TODO: maxBatchSize здесь быть не может, он отражает максимальное количество глифов, а не рендереров
        textRenderers = new CTextRenderer[maxBatchSize];
    }

    public void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        int eboID = glGenBuffers();
        int[] indices = generateIndices();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        enableVertexAttributes();
    }

    public void render() {
        if (contentToRender.isEmpty()) {
            return;
        }

        glBindFragDataLocation(shader.getShaderProgramID(), 0, "fragColor");

        glActiveTexture(GL_TEXTURE0);
        Font.bind();

        numVertices = 0;
        numGlyphs = 0;

        boolean rebufferData = false;
        for (int i = 0; i < numTextBlocks; i++) {
            CTextRenderer textRenderer = textRenderers[i];
            //TODO: вернуть логику ребафферинга
//            if (textRenderer.getIsDirty()) {
            loadVertexProperties(numGlyphs, textRenderer);
            textRenderer.setIsDirty();
            rebufferData = true;
//            }
            numGlyphs += textRenderer.getText().getTextSize();
        }

        numVertices = numGlyphs * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE;

//        if (rebufferData){
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, numVertices * Float.BYTES, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertices, 0, numVertices));
//        }

        shader.use();

        shader.setUniform(shader.getUniformLocation("uTexture"), 0);

        shader.setUniform(shader.getUniformLocation("uProjection"),
                Window.getCurrentScene().getCamera().getProjectionMatrix()
        );

        //TODO: оказалось, что проблемой рендера текста была зеркально отражённая по главной диагонали view матрица.
        // Сейчас я выставляю значения координат в матрицы вручную, но нужно это унифицировать.
        // Не хочу чтобы разные рендереры требовали матрицы разного вида.

        //vvvvvv
        final Matrix4f view = Window.getCurrentScene().getCamera().getViewMatrix();
        shader.setUniform(shader.getUniformLocation("uView"),
                new Matrix4f()
                        .m03(view.m30())
                        .m13(view.m31())
        );
        //^^^^^^

        glBindVertexArray(vaoID);
        enableVertexAttributes();

        glDrawElements(GL_TRIANGLES, numGlyphs * DEFAULT_QUAD_VERTICES_AMOUNT, GL_UNSIGNED_INT, 0);

        disableVertexAttributes();

        glBindBuffer(0, GL_ARRAY_BUFFER);
        glBindVertexArray(0);

        Font.unbind();

        shader.detach();
    }

    //TODO: пожалуй все таки стоит обратно убрать скейл текста в зависимости от объекта. Это просто добавляет ненужные усложнения при инициализации объектов/текста, и не несёт практически никакой выгоды
    private void loadVertexProperties(int index, CTextRenderer textRenderer) {
        int offset = index * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE;

        Text text = textRenderer.getText();
        float fullTextXAdd = textRenderer.gameObject.transform.position.x;
        float fullTextYAdd = textRenderer.gameObject.transform.position.y;

        int textSize = text.getTextSize();
        Color color = text.getColor();
        List<Glyph> content = text.getContent();
        List<Vector2f> positions = text.getPositions();
        int width = Font.getTexture().getWidth();
        int height = Font.getTexture().getHeight();

        for (int i = 0; i < textSize; i++) {
            Vector2f pos = new Vector2f(positions.get(i)).mul(textRenderer.gameObject.transform.scale);
            Glyph glyph = content.get(i);

            // TODO: текст нельзя вращать
//            boolean isRotated = textRenderer.gameObject.transform.rotation != 0;
//            Matrix4f transformMatrix = new Matrix4f().identity();
//            if (isRotated) {
//                transformMatrix.translate(textRenderer.gameObject.transform.position.x,
//                        textRenderer.gameObject.transform.position.y, 0f);
//                transformMatrix.rotate((float) Math.toRadians(textRenderer.gameObject.transform.rotation),
//                        0, 0, 1);
//                transformMatrix.scale(textRenderer.gameObject.transform.scale.x,
//                        textRenderer.gameObject.transform.scale.y, 1);
//            }


            //TODO: переделать это безумие из переменных
            float xAdd, yAdd, glyphSizeModY;
            for (int j = 0; j < EBO_QUAD_VERTICES_AMOUNT; j++) {
                xAdd = 0;
                yAdd = 0;
                glyphSizeModY = 1;
                if (j == 2 || j == 3) {
                    xAdd = glyph.width;
                }

                if (j == 1 || j == 2) {
                    yAdd = glyph.height;
                    // TODO: эти формулы кривые как ёб-твою-мать, придумай что-нибудь менее кривовыглядящее
                    switch (text.getAnimation()) {
                        case SIZE_WAVING_FORWARDS ->
                                glyphSizeModY += (float) (Math.sin(EngineUtils.time() * 2 - (i / 2f)) * 0.15f);
                        case SIZE_WAVING_BACKWARDS ->
                                glyphSizeModY += (float) (Math.sin(EngineUtils.time() * 2 + (i / 2f)) * 0.15f);
                    }
                }

                Vector4f currentPos = new Vector4f(
                        /*isRotated ?
                                new Vector4f(fullTextXAdd, fullTextYAdd, 0, 1).mul(transformMatrix)
                                :*/ new Vector4f(
                        fullTextXAdd,
                        fullTextYAdd,
                        0, 1)
                );

                vertices[offset] = text.getOffset().x + currentPos.x + pos.x + xAdd * text.getSizeModifier() * textRenderer.gameObject.transform.scale.x * TEXT_BASE_SIZE;
                vertices[offset + 1] = text.getOffset().y + currentPos.y + pos.y + yAdd * text.getSizeModifier() * textRenderer.gameObject.transform.scale.y * glyphSizeModY * TEXT_BASE_SIZE;
                vertices[offset + 2] = color.r;
                vertices[offset + 3] = color.g;
                vertices[offset + 4] = color.b;
                vertices[offset + 5] = color.a;
                vertices[offset + 6] = (glyph.x + xAdd) / width;
                vertices[offset + 7] = (glyph.y + yAdd) / height;

                offset += VERTEX_SIZE;
            }
        }
    }

    public void addElement(CTextRenderer textRenderer) {
        textRenderers[numTextBlocks] = textRenderer;

        if (textRenderer.getText() != null) {
//            if (!hasText(textRenderer.getText())) {
            contentToRender.add(textRenderer.getText());
//            }
        }

        loadVertexProperties(numGlyphs, textRenderer);
        numTextBlocks++;
        numGlyphs += textRenderer.getText().getTextSize();

        if (numGlyphs >= maxBatchSize) {
            hasRoom = false;
        }
    }

    //TODO: ВРЕМЕННЫЙ МЕТОД, ПЕРЕДЕЛАТЬ ПОД УДАЛЕНИЕ ПО ID ТЕКСТА!!!
    public void removeText(String textToDelete) {
        for (int i = 0; i < contentToRender.size(); i++) {
            Text currText = contentToRender.get(i);
            if (currText.getTextContent().equals(textToDelete)) {
                contentToRender.remove(i);
                numTextBlocks--;
                numGlyphs -= currText.getTextSize();

                return;
            }
        }
    }

    private int[] generateIndices() {
        int[] elements = new int[maxBatchSize * DEFAULT_QUAD_VERTICES_AMOUNT];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    public boolean destroyIfExists(GameObject go) {
        boolean wasDestroyed = false;

        List<CTextRenderer> fetchedTextRenderers = go.getComponents(CTextRenderer.class);
        for (CTextRenderer fetchedTextRenderer : fetchedTextRenderers) {
            for (int i = 0; i < numTextBlocks; i++) {
                if (textRenderers[i] == fetchedTextRenderer) {
                    for (int j = i; j < numTextBlocks - 1; j++) {
                        textRenderers[j].setIsDirty();
                        textRenderers[j] = textRenderers[j + 1];
                    }
                    numTextBlocks--;
                    numGlyphs -= fetchedTextRenderer.getText().getTextSize();

                    wasDestroyed = true;
                }
            }
        }

        return wasDestroyed;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = DEFAULT_QUAD_VERTICES_AMOUNT * index;
        int offset = EBO_QUAD_VERTICES_AMOUNT * index;

        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;

        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
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
