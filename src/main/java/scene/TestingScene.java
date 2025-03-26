package scene;

import component.Item;
import component.controls.TestingSceneControls;
import engine.component.CGameCamera;
import engine.component.CItemPickUp;
import engine.component.CSpriteRenderer;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.util.AssetPool;
import org.joml.Vector2f;

public class TestingScene extends Scene {

    private SpriteSheet sprites;

    private transient GameObject editorGameobject;

    @Override
    public void init() {
        loadResources();

        sprites = AssetPool.getSpritesheet("asset/texture/test-sprite-sheet.png");

        camera = new Camera(new Vector2f(0, 0));
        editorGameobject = new GameObject("random GameObject", new Transform(new Vector2f(0, 0), new Vector2f(50, 50)), 1)
                .addComponent(new CGameCamera(camera))
                .addComponent(new TestingSceneControls());

        addGameObjectToScene(editorGameobject);


        GameObject player = new GameObject("player", new Transform(new Vector2f(0, 0), new Vector2f(40, 40)), 5)
                .addComponent(new CItemPickUp(60))
                .addComponent(new CSpriteRenderer().setSprite(sprites.getSprite(0)));

        addGameObjectToScene(player);


        GameObject item = new GameObject("item", new Transform(new Vector2f(500, 500), new Vector2f(25, 25)), 1)
                .addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/chip-small.png"))))
                .addComponent(new Item());


        addGameObjectToScene(item);

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
            if (g.getComponent(CSpriteRenderer.class) != null) {
                CSpriteRenderer spr = g.getComponent(CSpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }
}
