package scene;

import component.controls.SelectionControls;
import engine.component.CSpriteRenderer;
import engine.component.ui.CFpsCounter;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.util.AssetPool;
import org.joml.Vector2f;

public class SceneSelectionScene extends Scene {

    @Override
    public void init() {
        loadResources();

        camera = new Camera(new Vector2f(0, 0));
        GameObject cameraGO = new GameObject("camera", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 1)
                .addComponent(new SelectionControls());

        addGameObjectToScene(cameraGO);
        GameObject ringSelectGO = new GameObject("ring select", new Transform(new Vector2f(200, 300), new Vector2f(256, 256)), -1)
                .addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/boxing-ring.png"))));

        addGameObjectToScene(ringSelectGO);

        GameObject testingSelectGO = new GameObject("testing select", new Transform(new Vector2f(1000, 300), new Vector2f(128, 128)), -1)
                .addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/chip-small.png"))));

        addGameObjectToScene(testingSelectGO);

        GameObject fpsGO = new GameObject("fpsGO", new Transform(new Vector2f(200, 100), new Vector2f(100, 100)), -1)
                .addComponent(new CFpsCounter().setDisplayMode(CFpsCounter.DisplayMode.EVERY_FRAME));

        addGameObjectToScene(fpsGO);

        GameObject fpsGO1 = new GameObject("fpsGO", new Transform(new Vector2f(200, 164), new Vector2f(100, 100)), -1)
                .addComponent(new CFpsCounter().setDisplayMode(CFpsCounter.DisplayMode.EVERY_SECOND_AVG));

        addGameObjectToScene(fpsGO1);

        GameObject fpsGO2 = new GameObject("fpsGO", new Transform(new Vector2f(200, 228), new Vector2f(100, 100)), -1)
                .addComponent(new CFpsCounter().setDisplayMode(CFpsCounter.DisplayMode.EVERY_SECOND_MIN));

        addGameObjectToScene(fpsGO2);

        GameObject fpsGO3 = new GameObject("fpsGO", new Transform(new Vector2f(200, 294), new Vector2f(100, 100)), -1)
                .addComponent(new CFpsCounter().setDisplayMode(CFpsCounter.DisplayMode.EVERY_SECOND_MAX));

        addGameObjectToScene(fpsGO3);
    }

    private void loadResources() {
        AssetPool.getShaderByName("default");

        AssetPool.getTexture("asset/texture/chip-small.png");

        AssetPool.addSpritesheet("asset/texture/test-sprite-sheet.png",
                new SpriteSheet(AssetPool.getTexture("asset/texture/test-sprite-sheet.png"), 16, 16, 5, 0));
        AssetPool.addSpritesheet("asset/texture/gizmo.png",
                new SpriteSheet(AssetPool.getTexture("asset/texture/gizmo.png"), 3, 8, 3, 0));

        for (GameObject g : gameObjects) {
            if (g.getComponent(CSpriteRenderer.class) != null) {
                CSpriteRenderer spr = g.getComponent(CSpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }
}
