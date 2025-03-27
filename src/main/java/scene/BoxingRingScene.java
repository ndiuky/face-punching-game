package scene;

import component.controls.RingControls;
import component.entity.Dummy;
import component.entity.Player;
import component.object.Stone;
import engine.component.CDraggingCamera;
import engine.component.CSpriteRenderer;
import engine.component.ui.CFpsCounter;
import engine.component.ui.CTextRenderer;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.AssetPool;
import gameComponent.CMoveWasd;
import org.joml.Vector2f;

import java.util.List;
import java.util.Random;

public class BoxingRingScene extends Scene {

    private SpriteSheet sprites;

    private GameObject player;

    private GameObject boxingRing;

    @Override
    public void init() {
        loadResources();

        sprites = AssetPool.getSpritesheet("asset/texture/test-sprite-sheet.png");

        camera = new Camera(new Vector2f(0, 0));
        GameObject cameraGO = new GameObject("camera", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 1)
                .addComponent(new CDraggingCamera(camera))
                .addComponent(new RingControls());

        addGameObjectToScene(cameraGO);

        boxingRing = new GameObject("boxingRing", new Transform(new Vector2f(0, 0), new Vector2f(12.5f, 12.5f)), -1)
                .addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/boxing-ring.png"))))
        ;

        addGameObjectToScene(boxingRing);

        player = new GameObject("player", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 5)
                .addComponent(new Player())
                .addComponent(new CMoveWasd())
                .addComponent(new CTextRenderer().setText(new Text("player").setAlignment(Alignment.BOTTOM_MIDDLE)));

        addGameObjectToScene(player);

        GameObject dummy = new GameObject("dummy", new Transform(new Vector2f(0, 128), new Vector2f(50, 50)), 4)
                .addComponent(new Dummy())
                .addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/dummy.png"))));

        addGameObjectToScene(dummy);

        GameObject fpsGO1 = new GameObject("fpsGO", new Transform(new Vector2f(0, 128), new Vector2f(1, 1)), -1)
                .addComponent(new CFpsCounter().setDisplayMode(CFpsCounter.DisplayMode.EVERY_SECOND_AVG));

        addGameObjectToScene(fpsGO1);

        for (int i = 0; i < 5; i++) {
            GameObject stone = new GameObject("stone", new Transform(new Vector2f(new Random().nextInt(-150, 150), new Random().nextInt(-150, 150)), new Vector2f(50, 50)), -1)
                    .addComponent(new Stone());

            addGameObjectToScene(stone);
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
