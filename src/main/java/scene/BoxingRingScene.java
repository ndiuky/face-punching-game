package scene;

import component.controls.RingControls;
import component.entity.Dummy;
import component.entity.Player;
import engine.component.CGameCamera;
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
                .addComponent(new CGameCamera(camera))
                .addComponent(new RingControls());

        addGameObjectToScene(cameraGO);

        boxingRing = new GameObject("boxingRing", new Transform(new Vector2f(0, 0), new Vector2f(512, 512)), -1)
                .addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/boxing-ring.png"))))
                ;

        addGameObjectToScene(boxingRing);

        player = new GameObject("player", new Transform(new Vector2f(0, -128), new Vector2f(40, 40)), 5)
                .addComponent(new Player()
//                                .setActionGameComponent(new CMoveArc())
//                                .setActionGameComponent(new CMoveStraight())
//                                .setActionGameComponent(new CForwardAttack())
//                                .setActionGameComponent(new CArcAttack())
                )
//                .addComponent(new CItemPickUp(60))
                .addComponent(new CMoveWasd())
                .addComponent(new CTextRenderer().setText(new Text("player").setAlignment(Alignment.BOTTOM_MIDDLE)))
                .addComponent(new CSpriteRenderer().setSprite(sprites.getSprite(0)));

        addGameObjectToScene(player);

        GameObject dummy = new GameObject("dummy", new Transform(new Vector2f(0, 128), new Vector2f(50, 50)), 4)
                .addComponent(new Dummy())
                .addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/dummy.png"))));

        addGameObjectToScene(dummy);

        GameObject fpsGO1 = new GameObject("fpsGO", new Transform(new Vector2f(0, 256), new Vector2f(100, 100)), -1)
                .addComponent(new CFpsCounter().setDisplayMode(CFpsCounter.DisplayMode.EVERY_SECOND_AVG));

        addGameObjectToScene(fpsGO1);
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
