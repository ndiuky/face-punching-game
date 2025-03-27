package scene;

import component.controls.TestingSceneControls;
import component.entity.Player;
import engine.component.CDraggingCamera;
import engine.component.CSpriteRenderer;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.util.AssetPool;
import org.joml.Vector2f;

import java.util.List;

public class TestingScene extends Scene {

    private SpriteSheet sprites;

    private transient GameObject editorGameobject;

    @Override
    public void init() {
        loadResources();

        sprites = AssetPool.getSpritesheet("asset/texture/test-sprite-sheet.png");

        camera = new Camera(new Vector2f(0, 0));
        editorGameobject = new GameObject("random GameObject", new Transform(new Vector2f(0, 0), new Vector2f(50, 50)), 1)
                .addComponent(new CDraggingCamera(camera))
                .addComponent(new TestingSceneControls());

        addGameObjectToScene(editorGameobject);


        GameObject player = new GameObject("player", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 5)
                .addComponent(new Player())
                .addComponent(new CSpriteRenderer().setSprite(sprites.getSprite(0)));

        addGameObjectToScene(player);

        if (isLevelLoaded) {
            return;
        }
    }

    private void loadResources() {
        AssetPool.getShaderByName("default");

        AssetPool.getTexture("asset/texture/chip-small.png");

        AssetPool.addSpriteSheet("asset/texture/test-sprite-sheet.png",
                new SpriteSheet(AssetPool.getTexture("asset/texture/test-sprite-sheet.png"), 16, 16, 5, 0));
        AssetPool.addSpriteSheet("asset/texture/gizmo.png",
                new SpriteSheet(AssetPool.getTexture("asset/texture/gizmo.png"), 3, 8, 3, 0));

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
