package scene;

import component.controls.SelectionControls;
import engine.component.CSpriteRenderer;
import engine.component.CTextRenderer;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.render.DebugDraw;
import engine.text.Text;
import engine.util.AssetPool;
import engine.util.Color;
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

        DebugDraw.drawBox(new Vector2f(100, 100), new Vector2f(50, 100), -1, 30, Color.WHITE, 1000000);


        GameObject textGO = new GameObject("textGO", new Transform(new Vector2f(100, 100), new Vector2f(128, 128)), -1)
                .addComponent(new CTextRenderer().setText(Text.builder().textContent("selection scene text").build()));

        addGameObjectToScene(textGO);

//        GameObject textGO = new GameObject("textGO", new Transform(new Vector2f(100, 100), new Vector2f(128, 128)), -1)
//                .addComponent(TextRenderer.get().setText("penis.").setPos(100, 100));
//
//        addGameObjectToScene(textGO);

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
