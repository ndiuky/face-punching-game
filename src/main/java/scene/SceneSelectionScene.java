package scene;

import engine.component.GameCamera;
import engine.component.SpriteRenderer;
import engine.event.MouseEventListener;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.render.Window;
import engine.util.AssetPool;
import engine.util.listener.KeyListener;
import engine.util.listener.MouseListener;
import org.joml.Vector2f;

import static engine.event.MouseEventProducer.listenToPress;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class SceneSelectionScene extends Scene {

    @Override
    public void init() {
        loadResources();

        camera = new Camera(new Vector2f(0, 0));
        GameObject cameraGO = new GameObject("camera", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 1);

        addGameObjectToScene(cameraGO);
        GameObject ringSelectGO = new GameObject("ring select", new Transform(new Vector2f(200, 300), new Vector2f(256, 256)), -1)
                .addComponent(new SpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/boxing-ring.png"))));

        addGameObjectToScene(ringSelectGO);

        GameObject testingSelectGO = new GameObject("testing select", new Transform(new Vector2f(1000, 300), new Vector2f(128, 128)), -1)
                .addComponent(new SpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/chip-small.png"))));

        addGameObjectToScene(testingSelectGO);
    }

    @Override
    public void update(final float dt) {

        camera.adjustProjection();

        if (KeyListener.isKeyPressed(GLFW_KEY_BACKSPACE)) {
            System.out.println("exit");
            glfwSetWindowShouldClose(Window.getGlfwWindow(), true);
        } else if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)){
            final GameObject go;
            if ((go = MouseListener.getGameObjectUnderCursor()) != null) {
                if (go.getName().equals("ring select")){
                    Window.changeScene(1);
                } else if(go.getName().equals("testing select")){
                    Window.changeScene(0);
                }
            }
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
