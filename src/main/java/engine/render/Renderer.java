package engine.render;

import engine.component.CSpriteRenderer;
import engine.component.ui.CTextRenderer;
import engine.component.ui.Canvas;
import engine.component.ui.UiElement;
import engine.component.ui.UiSprite;
import engine.component.ui.UiText;
import engine.object.GameObject;
import engine.text.Text;
import engine.util.AssetPool;
import registry.ShaderType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static engine.util.EnginePreferences.MAX_BATCH_SIZE;

public class Renderer {

    private final List<TextureRenderBatch> textureBatches = new ArrayList<>();

    private final List<TextRenderBatch> textBatches = new ArrayList<>();

    private final List<UiTextureRenderBatch> uiTextureRenderBatches = new ArrayList<>();

    private final List<UiTextRenderBatch> uiTextRenderBatches = new ArrayList<>();

    private final Map<Shader, List<List<? extends Batch>>> shaderBatchesMapping = Map.ofEntries(
            Map.entry(AssetPool.getShader(ShaderType.DEFAULT), List.of(textureBatches)),
            //TODO: пожалуй тут не нужно рендерить ui текстурки для picking шейдера, ибо детект клика происходит по другой схеме
            Map.entry(AssetPool.getShader(ShaderType.PICKING), List.of(textureBatches, uiTextureRenderBatches)),
            Map.entry(AssetPool.getShader(ShaderType.UI_TEXTURE), List.of(uiTextureRenderBatches)),
            Map.entry(AssetPool.getShader(ShaderType.UI_TEXT), List.of(uiTextRenderBatches)),
            Map.entry(AssetPool.getShader(ShaderType.TEXT), List.of(textBatches))
    );

    public static Shader currentShader;

    public void add(GameObject go) {
        List<CSpriteRenderer> spriteRenderers = go.getComponents(CSpriteRenderer.class);
        if (spriteRenderers != null) {
            addSpriteRenderers(spriteRenderers);
        }

        List<CTextRenderer> textRenderers = go.getComponents(CTextRenderer.class);
        if (textRenderers != null) {
            addTextRenderers(textRenderers);
        }

        List<Canvas> canvases = go.getComponents(Canvas.class);
        if (canvases != null) {
            addCanvases(canvases);
        }
    }

    private void addTextRenderers(List<CTextRenderer> textRenderers) {
        for (CTextRenderer textRenderer : textRenderers) {
            boolean added = false;
            for (TextRenderBatch batch : textBatches) {
                if (batch.hasRoom && batch.zIndex == textRenderer.gameObject.zIndex) {
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
                TextRenderBatch newBatch = new TextRenderBatch(MAX_BATCH_SIZE, textRenderer.gameObject.zIndex);
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
                if (batch.hasRoom() && batch.getZIndex() == spriteRenderer.gameObject.zIndex) {
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
                TextureRenderBatch newBatch = new TextureRenderBatch(MAX_BATCH_SIZE, spriteRenderer.gameObject.zIndex);
                newBatch.start();
                textureBatches.add(newBatch);
                newBatch.addElement(spriteRenderer);
                Collections.sort(textureBatches);
            }
        }
    }

    public void addCanvases(List<Canvas> canvases) {
        //TODO: тут нужно добавить отдельные батчи с ui текстом
        for (Canvas canvas : canvases) {
            UiTextureRenderBatch newTextureBatch = new UiTextureRenderBatch(canvas);
            UiTextRenderBatch newTextBatch = new UiTextRenderBatch(canvas);
            newTextureBatch.start();
            newTextBatch.start();
            uiTextureRenderBatches.add(newTextureBatch);
            uiTextRenderBatches.add(newTextBatch);

            for (UiElement uiElement : canvas.uiElements) {
                if (uiElement instanceof UiSprite) {
                    newTextureBatch.addElement(uiElement);
                }
                if (uiElement instanceof UiText) {
                    newTextBatch.addElement(uiElement);
                }
            }

            //TODO: implement
//            Collections.sort(uiRenderBatches);
        }
    }

    public void render() {
        for (List<? extends Batch> batches : shaderBatchesMapping.get(currentShader)) {
            for (Batch batch : batches) {
                batch.render();
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
}