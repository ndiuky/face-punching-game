package engine.render;

import engine.component.CSpriteRenderer;
import engine.object.GameObject;
import org.joml.Matrix4f;
import org.joml.Vector2f;
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

//TODO: создать класс для помощи в работе с OpenGL
//TODO: удалить поддержку SpriteSheet, и всегда использовать по одной текстуре не спрайт (кроме текста). Таким образом можно не передавать текстурные координаты в шейдер, а просто выставлять их там вручную ({0,0},{1,1})
public class TextureRenderBatch extends Batch implements Comparable<TextureRenderBatch> {

    private static final int POS_SIZE = 2;

    private static final int COLOR_SIZE = 4;

    private static final int TEXTURE_COORDS_SIZE = 2;

    private static final int TEXTURE_ID_SIZE = 1;

    private static final int ENTITY_ID_SIZE = 1;

    private static final int POS_OFFSET = 0;

    private static final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;

    private static final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;

    private static final int TEXTURE_ID_OFFSET = TEXTURE_COORDS_OFFSET + TEXTURE_COORDS_SIZE * Float.BYTES;

    private static final int ENTITY_ID_OFFSET = TEXTURE_ID_OFFSET + ENTITY_ID_SIZE * Float.BYTES;

    private static final int VERTEX_SIZE = 10;

    private static final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private final CSpriteRenderer[] spriteRenderers;

    private final List<Texture> textures = new ArrayList<>();

    private final int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private final int zIndex;

    private final float[] vertices;

    private final int maxBatchSize;

    private int numElements = 0;

    private boolean hasRoom = true;

    private int vaoID, vboID;

    public TextureRenderBatch(int maxBatchSize, int zIndex) {
        this.zIndex = zIndex;
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE];
        spriteRenderers = new CSpriteRenderer[maxBatchSize];
    }

    public void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboID = glGenBuffers();
        int[] indices = generateIndices(maxBatchSize);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        enableVertexAttribArrays();
    }

    @Override
    public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < numElements; i++) {
            CSpriteRenderer spriteRenderer = spriteRenderers[i];
            //TODO: почему-то это условие всегда true
            if (spriteRenderer.isDirty) {
                loadVertexProperties(i);
                spriteRenderer.isDirty = true;
                rebufferData = true;
            }
        }

        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        Shader shader = Renderer.currentShader;
        shader.use();
        shader.uploadMat4f("uProjection", Window.currentScene.camera.projectionMatrix);
        shader.uploadMat4f("uView", Window.currentScene.camera.getViewMatrix());

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        shader.uploadIntArray("uTextures", textureSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, numElements * DEFAULT_QUAD_VERTICES_AMOUNT, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (Texture texture : textures) {
            texture.unbind();
        }

        shader.detach();
    }

    private void enableVertexAttribArrays() {
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    public void addElement(CSpriteRenderer spriteRenderer) {
        spriteRenderers[numElements] = spriteRenderer;

        //TODO: 09.06.2024 попробовать переделать лист текстур на сет, возможно получится улучшить производительность благодаря быстрому поиску по хешу
        if (spriteRenderer.getTexture() != null) {
            if (!hasTexture(spriteRenderer.getTexture())) {
                textures.add(spriteRenderer.getTexture());
            }
        }

        loadVertexProperties(numElements++);

        if (numElements >= maxBatchSize) {
            hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {

        CSpriteRenderer sprite = spriteRenderers[index];

        int offset = index * EBO_QUAD_VERTICES_AMOUNT * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] textureCoords = sprite.getTextureCoords();

        int textureId = 0;
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(sprite.getTexture())) {
                    textureId = i + 1;

                    break;
                }
            }
        }

        boolean isRotated = sprite.gameObject.transform.rotation != 0;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(sprite.gameObject.transform.position.x,
                    sprite.gameObject.transform.position.y, 0f);
            transformMatrix.rotate((float) Math.toRadians(sprite.gameObject.transform.rotation),
                    0, 0, 1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x,
                    sprite.gameObject.transform.scale.y, 1);
        }

        // TODO: перенести в Texture
        float textureAspectRatio = (float) sprite.getTexture().width / sprite.getTexture().height;

        //debug
//        Vector4f mul = new Vector4f(sprite.gameObject.transform.position.x, sprite.gameObject.transform.position.y, 0, 1);
//        if (!AssetPool.getTexture(TextureType.GRASS_0).equals(sprite.getTexture())) {
//            DebugDraw.drawCircle(new Vector2f(mul.x, mul.y), 0.1f, -1, Color.RED, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_LOW);
//        }

        Vector2f anchorPoint = sprite.getAnchorPoint();

        float xAdd = (0.5f - anchorPoint.x) * textureAspectRatio;
        float yAdd = 0.5f - anchorPoint.y;
        for (int i = 0; i < EBO_QUAD_VERTICES_AMOUNT; i++) {
            if (i == 1) {
                yAdd = -anchorPoint.y - 0.5f;
            } else if (i == 2) {
                xAdd = (-anchorPoint.x - 0.5f) * textureAspectRatio;
            } else if (i == 3) {
                yAdd = 0.5f - anchorPoint.y;
            }

            Vector4f currentPos = new Vector4f(
                    isRotated ?
                            new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix)
                            : new Vector4f(
                            sprite.gameObject.transform.position.x + sprite.gameObject.transform.scale.x * xAdd,
                            sprite.gameObject.transform.position.y + sprite.gameObject.transform.scale.y * yAdd,
                            0, 1)
            );

            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            vertices[offset + 6] = textureCoords[i].x;
            vertices[offset + 7] = textureCoords[i].y;

            vertices[offset + 8] = textureId;

            vertices[offset + 9] = sprite.gameObject.uid + 1;

            offset += VERTEX_SIZE;
        }
    }

    public boolean destroyIfExists(GameObject go) {
        boolean wasDestroyed = false;

        List<CSpriteRenderer> sprites = go.getComponents(CSpriteRenderer.class);
        for (CSpriteRenderer sprite : sprites) {
            for (int i = 0; i < numElements; i++) {
                if (spriteRenderers[i] == sprite) {
                    for (int j = i; j < numElements - 1; j++) {
                        spriteRenderers[j].isDirty = true;
                        spriteRenderers[j] = spriteRenderers[j + 1];
                    }
                    numElements--;

                    wasDestroyed = true;
                }
            }
        }

        return wasDestroyed;
    }

    public boolean hasRoom() {
        return hasRoom;
    }

    public int getZIndex() {
        return zIndex;
    }

    public boolean hasTextureRoom() {
        return textures.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return textures.contains(texture);
    }

    @Override
    public int compareTo(TextureRenderBatch o) {
        return Integer.compare(zIndex, o.getZIndex());
    }
}