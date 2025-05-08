package scene;

import component.controls.ForestSceneControls;
import component.entity.Player;
import component.object.Stone;
import component.object.Tree;
import engine.component.CGameCamera;
import engine.component.ui.CFpsCounter;
import engine.component.ui.CPositionText;
import engine.object.GameObject;
import engine.object.Prefab;
import engine.object.Scene;
import engine.object.Sprite;
import engine.object.Transform;
import engine.render.Camera;
import engine.util.AssetPool;
import org.joml.Vector2f;
import registry.TextureType;

import java.util.Random;

//TODO: реализовать процедурную генерацию текстур. В этой сцене она понадобится для генерации земли
public class ForestScene extends Scene {

    private GameObject player;

    @Override
    public void init() {
        camera = new Camera().setZoom(150);

        GameObject controls = new GameObject("controls", new Transform(new Vector2f(), new Vector2f()), 0)
                .addComponent(new ForestSceneControls());

        addGameObjectToScene(controls);

        player = new GameObject("player", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), 5)
                .addComponent(new Player())
                .addComponent(new CPositionText())
                .addComponent(new CGameCamera(camera));

        addGameObjectToScene(player);

        GameObject fpsGO1 = new GameObject("fpsGO", new Transform(new Vector2f(0, 0), new Vector2f(1, 1)), -1)
                .addComponent(new CFpsCounter().setDisplayMode(CFpsCounter.DisplayMode.EVERY_SECOND_AVG));

        addGameObjectToScene(fpsGO1);

        generateWorld();
    }

    private void generateWorld() {
        Random random = new Random();

//        addGameObjectToScene(new GameObject("pis'ka", new Transform(new Vector2f(0), new Vector2f(1)), -1).addComponent(new CTextRenderer().setText(new Text("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))));

        //stones
        for (int i = 0; i < 50; i++) {
            float x = random.nextFloat(0.8f, 1.2f);
            GameObject stone = new GameObject("stone", new Transform(new Vector2f(random.nextInt(-14, 15), random.nextInt(-14, 15)), new Vector2f(x)), -1)
                    .addComponent(new Stone());

            addGameObjectToScene(stone);
        }

        //trees
        for (int i = 0; i < 25; i++) {
            float x = random.nextFloat(0.8f, 1.2f);
            GameObject stone = new GameObject("tree", new Transform(new Vector2f(random.nextInt(-14, 15) + 0.5f, random.nextInt(-14, 15) + 0.5f), new Vector2f(x)), -1)
                    .addComponent(new Tree());

            addGameObjectToScene(stone);
        }

        //floor tiles
        Sprite grassFloor = new Sprite(AssetPool.getTexture(TextureType.GRASS_0));
        for (int i = -25; i < 25; i++) {
            for (int j = -25; j < 25; j++) {
                GameObject go = Prefab.generateSpriteObject(grassFloor, 1, -5);
                go.transform.position = new Vector2f(i, j);
                addGameObjectToScene(go);
            }
        }

    }
}
