package engine.render;

import engine.component.SpriteRenderer;
import engine.object.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {

    private final int MAX_BATCH_SIZE = 1000;

    private List<RenderBatch> batches;

    private static Shader currentShader;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

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
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getZIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public void render() {
        currentShader.use();

        for (RenderBatch batch : batches) {
            batch.render();
        }
    }

    public static void bindShader(Shader shader){
        currentShader = shader;
    }

    public static Shader getCurrentShader(){
        return currentShader;
    }
}