package engine.render;

import engine.component.ui.Canvas;
import engine.component.ui.UiElement;
import engine.component.ui.UiSprite;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static engine.util.EnginePreferences.DEFAULT_QUAD_VERTICES_AMOUNT;
import static engine.util.EnginePreferences.EBO_QUAD_VERTICES_AMOUNT;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11C.glDrawElements;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class UiRenderBatch extends Batch {

    //TODO: ограничение. Либо поднять, либо добавить возможность одновременно работать сразу с несколькими батчами, либо сделать динамическим
    private static final int MAX_BATCH_SIZE = 1024;

    private static final int POS_SIZE = 2;

    private static final int COLOR_SIZE = 4;

    private static final int TEXTURE_ID_SIZE = 1;

    private static final int POS_OFFSET = 0;

    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private static final int TEXTURE_ID_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = 7;

    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private int vaoID, vboID;

    private int numUiSprites = 0;

    private final float[] vertices;

    private final int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private final List<Texture> textures = new ArrayList<>();

    private final UiSprite[] uiSprites;

    private final Canvas attachedCanvas;

    public UiRenderBatch(Canvas attachedCanvas) {
        this.attachedCanvas = attachedCanvas;

        vertices = new float[MAX_BATCH_SIZE * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE];
        uiSprites = new UiSprite[MAX_BATCH_SIZE];
    }

    public void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboID = glGenBuffers();
        int[] indices = generateIndices(MAX_BATCH_SIZE);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        enableVertexAttribArrays();
    }

    @Override
    public void render() {
        // TODO: вернуть, но в переделанном виде. Текущий вариант работает совершенно неточно
//        if (attachedCanvas.getUiElements().stream().filter(UiElement::isEnabled).map(e -> e.getUiSprites().size()).reduce(Integer::sum).get() != numUiSprites) {
//            numUiSprites = 0;
//
//            //TODO: убрать стрим
//            attachedCanvas.getUiElements().stream().filter(UiElement::isEnabled).forEach(this::addElement);
//        } else {
//            //TODO: implement rebuffering
//            for (int i = 0; i < numUiSprites; i++) {
//                loadVertexProperties(i);
//            }
//        }

        //vvvvvv
        numUiSprites = 0;
        attachedCanvas.uiElements.stream().filter(uiElement -> uiElement.isEnabled).forEach(this::addElement);
        //^^^^^^

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        Shader shader = Renderer.currentShader;
        shader.use();

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numUiSprites * DEFAULT_QUAD_VERTICES_AMOUNT, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (Texture texture : textures) {
            texture.unbind();
        }

        shader.detach();
    }

    public void addElement(UiElement uiElement) {
        for (UiSprite uiSprite : uiElement.uiSprites) {
            uiSprites[numUiSprites] = uiSprite;

            if (!hasTexture(uiSprite.getTexture())) {
                textures.add(uiSprite.getTexture());
            }

            loadVertexProperties(numUiSprites++);
        }
    }

    private void loadVertexProperties(int index) {
        UiSprite uiSprite = uiSprites[index];

        int offset = index * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE;

        int textureId = 0;
        for (int i = 0; i < textures.size(); i++) {
            if (textures.get(i).equals(uiSprite.getTexture())) {
                textureId = i + 1;

                break;
            }
        }

        Vector4f position;
        if (uiSprite.keepProportions) {
            position = uiSprite.getScreenPosWithKeptProportions((float) uiSprite.getTexture().width / uiSprite.getTexture().height);
        } else {
            position = uiSprite.screenPos;
        }

        float currX = position.z;
        float currY = position.w;
        Vector4f spriteColor = uiSprite.color;
        for (int i = 0; i < EBO_QUAD_VERTICES_AMOUNT; i++) {
            if (i == 1) {
                currY = position.y;
            } else if (i == 2) {
                currX = position.x;
            } else if (i == 3) {
                currY = position.w;
            }

            vertices[offset] = currX;
            vertices[offset + 1] = currY;

            vertices[offset + 2] = spriteColor.x;
            vertices[offset + 3] = spriteColor.y;
            vertices[offset + 4] = spriteColor.z;
            vertices[offset + 5] = spriteColor.w;

            vertices[offset + 6] = textureId;

            offset += VERTEX_SIZE;
        }
    }

    private void enableVertexAttribArrays() {
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(2);
    }

    public boolean hasTexture(Texture texture) {
        return textures.contains(texture);
    }

    public Canvas getAttachedCanvas() {
        return attachedCanvas;
    }
}