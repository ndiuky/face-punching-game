package scene;

import engine.component.GameCamera;
import engine.component.ItemPickUp;
import engine.component.SpriteRenderer;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.SpriteSheet;
import engine.object.Transform;
import engine.render.Camera;
import engine.render.DebugDraw;
import engine.render.Window;
import engine.util.AssetPool;
import engine.util.Color;
import engine.util.listener.KeyListener;
import game_component.moving.CMoveArc;
import component.Item;
import component.entity.Player;
import game_component.moving.CMoveStraight;
import org.joml.Vector2f;

import static engine.util.EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class TestingScene extends Scene {

    private SpriteSheet sprites;

    private transient GameObject editorGameobject;

    @Override
    public void init() {
        loadResources();

        sprites = AssetPool.getSpritesheet("asset/texture/test-sprite-sheet.png");

        camera = new Camera(new Vector2f(0, 0));
        editorGameobject = new GameObject("random GameObject", new Transform(new Vector2f(0, 0), new Vector2f(50, 50)), 1)
                .addComponent(new GameCamera(camera));

        addGameObjectToScene(editorGameobject);




        GameObject player = new GameObject("player", new Transform(new Vector2f(0,0), new Vector2f(40, 40)), 5)
//                .addComponent(new Player().addGameComponent(new CMoveStraight()))
                .addComponent(new Player().addGameComponent(new CMoveArc()))
                .addComponent(new ItemPickUp(60))
                .addComponent(new SpriteRenderer().setSprite(sprites.getSprite(0)));

        addGameObjectToScene(player);

//        GameObject dummy = new GameObject("dummy", new Transform(new Vector2f(0,0), new Vector2f(40, 40)), 5)
//                .addComponent(new Player().addGameComponent(new CMoveArc()))
//                .addComponent(new SpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/chip-small.png"))));
//
//        addGameObjectToScene(player);



        GameObject item = new GameObject("item", new Transform(new Vector2f(500, 500), new Vector2f(25, 25)), 1)
                .addComponent(new SpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/chip-small.png"))))
                .addComponent(new Item());


        addGameObjectToScene(item);

        if (isLevelLoaded) {
            return;
        }
    }

    @Override
    public void update(final float dt) {

        // TODO: 13.07.2024 перенести вызов adjustProjection в более подходящее место, нет смысла вызывать его каждый кадр
        camera.adjustProjection();

        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.changeScene(2);
        }


//        DebugDraw.drawBox(new Vector2f(Window.getWidth()/4, Window.getHeight()/4), new Vector2f(Window.getWidth()/4, Window.getHeight()/4),5, 0, Color.RED);
//        DebugDraw.drawCircle(new Vector2f(Window.getWidth()/4, Window.getHeight()/4), 40, 0, Color.GREEN, 1, DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID);
//        DebugDraw.drawCircle(new Vector2f(Window.getWidth()/4, Window.getHeight()/4), 40, 0, Color.GREEN, 1, DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID);
//        DebugDraw.drawCircle(new Vector2f(Window.getWidth()/4, Window.getHeight()/4), 40, 0, Color.GREEN, 1, DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID);
//        DebugDraw.drawCircle(new Vector2f(Window.getWidth()/4, Window.getHeight()/4), 40, 0, Color.GREEN, 1, DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID);

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
