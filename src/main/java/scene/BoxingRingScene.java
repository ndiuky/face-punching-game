package scene;

import component.controls.RingControls;
import component.entity.Dummy;
import component.entity.Player;
import engine.component.GameCamera;
import engine.component.ItemPickUp;
import engine.component.SpriteRenderer;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.util.AssetPool;
import gameComponent.attack.CArcAttack;
import gameComponent.move.CMoveStraight;
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
                .addComponent(new GameCamera(camera))
                .addComponent(new RingControls());

        addGameObjectToScene(cameraGO);

        boxingRing = new GameObject("boxingRing", new Transform(new Vector2f(0, 0), new Vector2f(512, 512)), -1)
                .addComponent(new SpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/boxing-ring.png"))
                ));

        addGameObjectToScene(boxingRing);

        player = new GameObject("player", new Transform(new Vector2f(0, -128), new Vector2f(40, 40)), 5)
                .addComponent(new Player()
//                                .setActionGameComponent(new CMoveArc())
                                .setActionGameComponent(new CMoveStraight())
//                                .setActionGameComponent(new CForwardAttack())
                                .setActionGameComponent(new CArcAttack())
                )
                .addComponent(new ItemPickUp(60))
                .addComponent(new SpriteRenderer().setSprite(sprites.getSprite(0)));

        addGameObjectToScene(player);

        GameObject dummy = new GameObject("dummy", new Transform(new Vector2f(0, 128), new Vector2f(50, 50)), 4)
                .addComponent(new Dummy())
                .addComponent(new SpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/dummy.png"))));

        addGameObjectToScene(dummy);
    }

    private void loadResources() {
        AssetPool.getShaderByName("default");

        AssetPool.getTexture("asset/texture/chip-small.png");

        AssetPool.addSpritesheet("asset/texture/test-sprite-sheet.png",
                new SpriteSheet(AssetPool.getTexture("asset/texture/test-sprite-sheet.png"), 16, 16, 5, 0));
        AssetPool.addSpritesheet("asset/texture/gizmo.png",
                new SpriteSheet(AssetPool.getTexture("asset/texture/gizmo.png"), 3, 8, 3, 0));

        for (GameObject g : gameObjects) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
        }
    }
}
