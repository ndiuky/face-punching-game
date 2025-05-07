package engine.render;

import engine.component.CSpriteRenderer;
import engine.component.ui.CTextRenderer;
import engine.object.GameObject;
import engine.text.Text;
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
        List<CSpriteRenderer> spriteRenderers = go.getComponents(CSpriteRenderer.class);
        if (spriteRenderers != null) {
            addSpriteRenderers(spriteRenderers);
        }

        List<CTextRenderer> textRenderers = go.getComponents(CTextRenderer.class);
        if (textRenderers != null) {
            addTextRenderers(textRenderers);
        }
    }

    private void addTextRenderers(List<CTextRenderer> textRenderers) {
        for (CTextRenderer textRenderer : textRenderers) {
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
    }

    private void addSpriteRenderers(List<CSpriteRenderer> spriteRenderers) {
        for (CSpriteRenderer spriteRenderer : spriteRenderers) {
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
    }

    public void render() {
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        for (TextureRenderBatch textureBatch : textureBatches) {
            textureBatch.render();
        }

        if (!Renderer.getCurrentShader().equals(AssetPool.getShader("pickingShader"))) {
            for (TextRenderBatch textBatch : textBatches) {
                textBatch.render();
            }
        }
    }

    public void destroyGameObject(GameObject go) {
        List<CSpriteRenderer> spriteRenderers = go.getComponents(CSpriteRenderer.class);
        if (spriteRenderers != null) {
            for (CSpriteRenderer spriteRenderer : spriteRenderers) {
                for (TextureRenderBatch textureBatch : textureBatches) {
                    if (textureBatch.destroyIfExists(go)) {
                        break;
                    }
                }
            }
        }

        List<CTextRenderer> textRenderers = go.getComponents(CTextRenderer.class);
        if (textRenderers != null) {
            // TODO: textRenderer is not used
            for (CTextRenderer textRenderer : textRenderers) {
                for (TextRenderBatch textBatch : textBatches) {
                    if (textBatch.destroyIfExists(go)) {
                        break;
                    }
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