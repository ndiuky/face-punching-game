package scene;

import component.controls.SelectionControls;
import engine.component.CSpriteRenderer;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.util.AssetPool;
import org.joml.Vector2f;

import java.util.List;

public class SceneSelectionScene extends Scene {

    @Override
    public void init() {
        loadResources();

        camera = new Camera();
        GameObject sceneManager = new GameObject("sceneManager", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 1)
                .addComponent(new SelectionControls());

        addGameObjectToScene(sceneManager);

        GameObject stoneSelectGO = new GameObject("stone select", new Transform(new Vector2f(600, 300), new Vector2f(128, 128)), -1)
                .addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("stone"))));

        addGameObjectToScene(stoneSelectGO);

    }

    private void loadResources() {
        AssetPool.getShader("default");

        AssetPool.getTexture("chip-small");

        AssetPool.addSpriteSheet("test-sprite-sheet",
                new SpriteSheet(AssetPool.getTexture("test-sprite-sheet"), 16, 16, 5, 0));
        AssetPool.addSpriteSheet("gizmo",
                new SpriteSheet(AssetPool.getTexture("gizmo"), 3, 8, 3, 0));

        for (GameObject g : gameObjects) {
            if (g.getComponents(CSpriteRenderer.class) != null) {
                List<CSpriteRenderer> sprites = g.getComponents(CSpriteRenderer.class);
                for (CSpriteRenderer sprite : sprites) {
                    if (sprite.getTexture() != null) {
                        sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilepath()));
                    }
                }
            }
        }
    }
}
