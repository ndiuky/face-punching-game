package engine.render;

import engine.component.SpriteRenderer;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static engine.util.EnginePreferences.MAX_BATCH_SIZE;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;

public class Renderer {

    //TODO: объединить в один лист RenderBatch. МОЖЕТ НЕГАТИВНО ПОВЛИЯТЬ НА ПРОИЗВОДИТЕЛЬНОСТЬ!!!!
    private final List<RenderBatch> batches = new ArrayList<>();

    private static Shader currentShader;

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom() && batch.getZIndex() == sprite.gameObject.getZIndex()) {
                Texture texture = sprite.getTexture();
                if (texture == null || batch.hasTexture(texture) || batch.hasTextureRoom()) {
                    batch.addElement(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getZIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addElement(sprite);
            Collections.sort(batches);
        }
    }

    public void render() {
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }

    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) return;

        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getCurrentShader() {
        return currentShader;
    }
}