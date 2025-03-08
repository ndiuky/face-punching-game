package scene;

import engine.component.GameCamera;
import engine.component.SpriteRenderer;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.render.Window;
import engine.util.AssetPool;
import engine.util.listener.KeyListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class BoxingRingScene extends Scene {

    @Override
    public void init() {
        loadResources();

        camera = new Camera(new Vector2f(0, 0));
        GameObject cameraGO = new GameObject("camera", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 1)
                .addComponent(new GameCamera(camera));

        addGameObjectToScene(cameraGO);

        GameObject boxingRing = new GameObject("boxing ring", new Transform(new Vector2f(0, 0), new Vector2f(512, 512)), -1)
                .addComponent(new SpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/boxing-ring.png"))
                ));

        addGameObjectToScene(boxingRing);
    }

    @Override
    public void update(final float dt) {

        camera.adjustProjection();

        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.changeScene(2);
        }



        for (GameObject gameObject : gameObjects) {
            gameObject.update(dt);
        }
    }

    private void loadResources() {
        AssetPool.getShader("asset/shader/default.glsl");

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
