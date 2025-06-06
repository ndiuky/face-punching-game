package engine.render;

import engine.component.ui.Canvas;
import engine.component.ui.UiElement;
import engine.component.ui.UiText;
import engine.text.Font;
import engine.text.Glyph;
import engine.text.Text;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import util.Utils;

import java.util.Arrays;

import static engine.util.EnginePreferences.DEFAULT_QUAD_VERTICES_AMOUNT;
import static engine.util.EnginePreferences.EBO_QUAD_VERTICES_AMOUNT;
import static engine.util.EnginePreferences.MAX_BATCH_SIZE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawElements;
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
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL30C.glBindFragDataLocation;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

//TODO: скорее всего можно обойтись без Comparable
public class UiTextRenderBatch extends Batch implements Comparable<UiTextRenderBatch> {

    private static final int POS_SIZE = 2;

    private static final int COLOR_SIZE = 4;

    private static final int TEXTURE_COORDS_SIZE = 2;

    private static final int POS_OFFSET = 0;

    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private static final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = 8;

    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private int vaoID = 0, vboID = 0;

    private final float[] vertices;

    private int numVertices;

    private int numTextBlocks;

    private int numGlyphs = 0;

    public boolean hasRoom = true;

    //TODO: пока просто есть для того чтобы comparable работал
    private int zIndex = -1;

    private UiText[] uiTextBlocks;

    private final Canvas attachedCanvas;

    public UiTextRenderBatch(Canvas canvas) {
        this.attachedCanvas = canvas;

        vertices = new float[MAX_BATCH_SIZE * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE];
        //TODO: maxBatchSize здесь быть не может, он отражает максимальное количество глифов, а не рендереров
        uiTextBlocks = new UiText[MAX_BATCH_SIZE];
    }

    public void start() {
        vaoID = glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboID = glGenBuffers();
        int[] indices = generateIndices(MAX_BATCH_SIZE);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        enableVertexAttributes();
    }

    @Override
    public void render() {
        Shader shader = Renderer.currentShader;
        glBindFragDataLocation(shader.shaderProgramID, 0, "fragColor");

        glActiveTexture(GL_TEXTURE0);
        Font.bind();

        //TODO: кринж
        numTextBlocks = 0;
        numVertices = 0;
        numGlyphs = 0;
        attachedCanvas.uiElements.stream().filter(uiElement -> uiElement.isEnabled).forEach(this::addElement);

        boolean rebufferData = false;
        for (int i = 0; i < numTextBlocks; i++) {
            UiText textBlock = uiTextBlocks[i];
            //TODO: вернуть логику ребафферинга
//            if (textBlock.getIsDirty()) {
            loadVertexProperties(numGlyphs, textBlock);
            rebufferData = true;
//            }
            numGlyphs += textBlock.text.textContent.length();
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
                Window.currentScene.camera.projectionMatrix
        );

        //TODO: оказалось, что проблемой рендера текста была зеркально отражённая по главной диагонали view матрица.
        // Сейчас я выставляю значения координат в матрицы вручную, но нужно это унифицировать.
        // Не хочу чтобы разные рендереры требовали матрицы разного вида.

        //vvvvvv
        final Matrix4f view = Window.currentScene.camera.getViewMatrix();
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
    private void loadVertexProperties(int index, UiText uiText) {
        int offset = index * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE;

        Text text = uiText.text;

//            Vector4f pos = uiText.getScreenPosWithKeptProportions((float) glyph.width / glyph.height);
        Vector4f pos = uiText.screenPos;

        for (int i = 0; i < text.textContent.length(); i++) {
            Glyph glyph = text.content.get(i);

            // TODO: текст нельзя вращать
//            boolean isRotated = uiText.gameObject.transform.rotation != 0;
//            Matrix4f transformMatrix = new Matrix4f().identity();
//            if (isRotated) {
//                transformMatrix.translate(uiText.gameObject.transform.position.x,
//                        uiText.gameObject.transform.position.y, 0f);
//                transformMatrix.rotate((float) Math.toRadians(uiText.gameObject.transform.rotation),
//                        0, 0, 1);
//                transformMatrix.scale(uiText.gameObject.transform.scale.x,
//                        uiText.gameObject.transform.scale.y, 1);
//            }


            //TODO: переделать это безумие из переменных
            float xAdd;
            float yAdd;
            float glyphSizeModY;
            float currX = pos.x;
            float currY = pos.y;
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
                    switch (text.animation) {
                        case SIZE_WAVING_FORWARDS ->
                                glyphSizeModY += (float) (Math.sin(Utils.time() * 2 - (i / 2f)) * 0.15f);
                        case SIZE_WAVING_BACKWARDS ->
                                glyphSizeModY += (float) (Math.sin(Utils.time() * 2 + (i / 2f)) * 0.15f);
                    }
                }

                if (j == 1) {
                    currY = pos.w;
                } else if (j == 2) {
                    currX = pos.z;
                } else if (j == 3) {
                    currY = pos.y;
                }

                float x = currX + glyph.width / text.width;
                float y = currY + glyph.height / text.height;
                System.out.printf("%s: [%s, %s]\n", j, x, y);

                vertices[offset] = x;
                vertices[offset + 1] = y;
                vertices[offset + 2] = text.color.r;
                vertices[offset + 3] = text.color.g;
                vertices[offset + 4] = text.color.b;
                vertices[offset + 5] = text.color.a;
                vertices[offset + 6] = (glyph.x + xAdd) / Font.texture.width;
                vertices[offset + 7] = (glyph.y + yAdd) / Font.texture.height;

                offset += VERTEX_SIZE;
            }
        }
    }

    public void addElement(UiElement uiElement) {
        for (UiText uiText : uiElement.uiTextBlocks) {
            uiTextBlocks[numTextBlocks++] = uiText;

            loadVertexProperties(numGlyphs, uiText);

            if (numGlyphs >= MAX_BATCH_SIZE) {
                hasRoom = false;
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

    @Override
    public int compareTo(UiTextRenderBatch o) {
        return Integer.compare(zIndex, o.zIndex);
    }
}
