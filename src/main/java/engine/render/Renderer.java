package engine.render;

import engine.component.CSpriteRenderer;
import engine.component.ui.CTextRenderer;
import engine.object.GameObject;
import engine.text.Text;

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
        CSpriteRenderer spriteRenderer = go.getComponent(CSpriteRenderer.class);
        if (spriteRenderer != null) {
            add(spriteRenderer);
        }

        CTextRenderer textRenderer = go.getComponent(CTextRenderer.class);
        if (textRenderer != null) {
            add(textRenderer);
        }
    }

    private void add(CTextRenderer textRenderer) {
        boolean added = false;
        for (TextRenderBatch batch : textBatches) {
            if (batch.hasRoom() && batch.getZIndex() == textRenderer.gameObject.getZIndex()) {
                Text text = textRenderer.getText();
                //TODO: возможно проверка на null не нужна
                if (text == null || batch.hasText(text) || batch.hasTextRoom()) {
                    batch.addElement(textRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            TextRenderBatch newBatch = new TextRenderBatch(MAX_BATCH_SIZE, textRenderer.gameObject.getZIndex());
            newBatch.start();
            textBatches.add(newBatch);
            newBatch.addElement(textRenderer);
            Collections.sort(textBatches);
        }
    }

    private void add(CSpriteRenderer spriteRenderer) {
        boolean added = false;
        for (TextureRenderBatch batch : textureBatches) {
            if (batch.hasRoom() && batch.getZIndex() == spriteRenderer.gameObject.getZIndex()) {
                Texture texture = spriteRenderer.getTexture();
                //TODO: возможно проверка на null не нужна
                if (texture == null || batch.hasTexture(texture) || batch.hasTextureRoom()) {
                    batch.addElement(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            TextureRenderBatch newBatch = new TextureRenderBatch(MAX_BATCH_SIZE, spriteRenderer.gameObject.getZIndex());
            newBatch.start();
            textureBatches.add(newBatch);
            newBatch.addElement(spriteRenderer);
            Collections.sort(textureBatches);
        }
    }

    public void render() {
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (TextureRenderBatch textureBatch : textureBatches) {
            textureBatch.render();
        }
        for (TextRenderBatch textBatch : textBatches) {
            textBatch.render();
        }
    }

    public void destroyGameObject(GameObject go) {
        CSpriteRenderer sr = go.getComponent(CSpriteRenderer.class);
        if (sr != null) {
            for (TextureRenderBatch textureBatch : textureBatches) {
                if (textureBatch.destroyIfExists(go)) {
                    break;
                }
            }
        }

        CTextRenderer tr = go.getComponent(CTextRenderer.class);
        if (tr != null) {
            for (TextRenderBatch textBatch : textBatches) {
                if (textBatch.destroyIfExists(go)) {
                    break;
                }
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