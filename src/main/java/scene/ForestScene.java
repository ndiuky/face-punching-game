package scene;

import component.controls.TestingSceneControls;
import component.entity.Player;
import component.object.Stone;
import engine.component.CGameCamera;
import engine.component.CSpriteRenderer;
import engine.component.ui.CFpsCounter;
import engine.component.ui.CPositionText;
import engine.object.GameObject;
import engine.object.Scene;
import engine.object.Transform;
import engine.render.Camera;
import engine.util.AssetPool;
import org.joml.Vector2f;

import java.util.List;
import java.util.Random;

//TODO: реализовать процедурную генерацию текстур. В этой сцене она понадобится для генерации земли
public class ForestScene extends Scene {

    private GameObject player;

    @Override
    public void init() {
        loadResources();

        camera = new Camera().setZoom(150);

        GameObject controls = new GameObject("controls", new Transform(new Vector2f(), new Vector2f()), 0)
                .addComponent(new TestingSceneControls());

        addGameObjectToScene(controls);

        player = new GameObject("player", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 5)
                .addComponent(new Player())
                .addComponent(new CPositionText())
                .addComponent(new CGameCamera(camera));

        addGameObjectToScene(player);

        GameObject fpsGO1 = new GameObject("fpsGO", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), -1)
                .addComponent(new CFpsCounter().setDisplayMode(CFpsCounter.DisplayMode.EVERY_SECOND_AVG));

        addGameObjectToScene(fpsGO1);

        Random random = new Random();


        for (int i = 0; i < 50; i++) {
            float x = random.nextFloat(0.8f, 1.2f);
            GameObject stone = new GameObject("stone", new Transform(new Vector2f(random.nextInt(-14, 15), random.nextInt(-14, 15)), new Vector2f(x, x)), -1)
                    .addComponent(new Stone())
                    .addComponent(new CPositionText());

            addGameObjectToScene(stone);
        }
    }


    private void loadResources() {
        AssetPool.getShader("default");

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
