package engine.render;

import engine.component.SpriteRenderer;
import engine.object.GameObject;
import engine.util.AssetPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static engine.util.EnginePreferences.MAX_BATCH_SIZE;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;

public class Renderer {

    //TODO: объединить в один лист RenderBatch. МОЖЕТ НЕГАТИВНО ПОВЛИЯТЬ НА ПРОИЗВОДИТЕЛЬНОСТЬ!!!!
    private final List<TextureRenderBatch> textureBatches = new ArrayList<>();

    private final List<TextRenderBatch> textBatches = new ArrayList<>();

    private static Shader currentShader;

    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            add(spr);
        }
    }

    private void add(SpriteRenderer sprite) {
        boolean added = false;
        for (TextureRenderBatch batch : textureBatches) {
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
            TextureRenderBatch newBatch = new TextureRenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getZIndex());
            newBatch.start();
            textureBatches.add(newBatch);
            newBatch.addElement(sprite);
            Collections.sort(textureBatches);
        }
    }

//    private void add(CTextRenderer textRenderer) {
//        boolean added = false;
//        for (RenderBatch batch : batches) {
//            if (batch.hasRoom() && batch.getZIndex() == textRenderer.gameObject.getZIndex()) {
//                Text text = textRenderer.getText();
//                if (batch.hasText(text) || batch.hasTextRoom()) {
//                    batch.addText(textRenderer);
//                    added = true;
//                    break;
//                }
//            }
//        }
//
//        if (!added) {
//            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, textRenderer.gameObject.getZIndex());
//            newBatch.start();
//            batches.add(newBatch);
//            newBatch.addText(textRenderer);
//            Collections.sort(batches);
//        }
//    }

    public void render() {

        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (TextureRenderBatch textureBatch : textureBatches) {
            textureBatch.render();
        }



//        for (TextRenderBatch textBatch : textBatches) {
//            textBatch.render();
//        }
    }

    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) return;

        for (TextureRenderBatch textureBatch : textureBatches) {
            if (textureBatch.destroyIfExists(go)) {
                return;
            }
        }

//        for (TextRenderBatch textBatch : textBatches) {
//            if (textBatch.destroyIfExists(go)) {
//                return;
//            }
//        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getCurrentShader() {
        return currentShader;
    }
}