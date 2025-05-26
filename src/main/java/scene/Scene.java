package scene;

import engine.component.Component;
import engine.object.GameObject;
import engine.render.Camera;
import engine.render.Renderer;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static engine.util.EnginePreferences.SAVE_FILE_NAME;

public abstract class Scene {

    private static int idCounter = 0;

    protected int uid = -1;

    protected Renderer renderer = new Renderer();

    public Camera camera;

    public boolean isRunning = false;

    public boolean isLevelLoaded;

    protected List<GameObject> gameObjects = new ArrayList<>();

    private static final List<Scene> scenesList = new ArrayList<>();

    protected Scene() {
        uid = idCounter++;
        scenesList.add(this);
    }

    public void init() {
        //Override on demand
    }

    public void start() {
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.start();
            renderer.add(go);
        }
        isRunning = true;

        //TODO: delete
//        renderer.addUiElements(List.of(new UiSprite(new Sprite().setTexture(AssetPool.getTexture("long-boi"))).setScreenPos(new Vector4f(0, 0, 1,1))));
    }

    public void addGameObjectToScene(GameObject go) {
        gameObjects.add(go);

        if (isRunning) {
            go.start();
            renderer.add(go);
        }
    }

    public final void update(float dt) {
        this.camera.adjustProjection();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if (go.isDead) {
                gameObjects.remove(i--);
                renderer.destroyGameObject(go);
            }
        }
    }

    public void render() {
        renderer.render();
    }

    public void saveSceneData() {
        try {
            FileWriter writer = new FileWriter(SAVE_FILE_NAME);
            List<GameObject> gameObjectsToSerialize = new ArrayList<>();
            for (GameObject gameObject : gameObjects) {
                if (gameObject.doSerialize) {
                    gameObjectsToSerialize.add(gameObject);
                }
            }
            //TODO: implement
//            writer.write(gson.toJson(gameObjectsToSerialize));
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to save scene data: " + ex.getMessage());
        }
    }

    public void loadSceneData() {
        Path path = Paths.get(SAVE_FILE_NAME);
        if (!Files.exists(path)) {
            return;
        }

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(path));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read bytes from save file: " + ex.getMessage());
        }

        if (!inFile.isEmpty()) {
            int maxComponentId = -1;
            int maxGameObjectId = -1;

            //TODO: implement
//            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);
            GameObject[] objects = null;
            for (GameObject gameObject : objects) {
                addGameObjectToScene(gameObject);
                for (Component c : gameObject.components) {
                    if (c.uid > maxComponentId) {
                        maxComponentId = c.uid;
                    }
                }
                if (gameObject.uid > maxGameObjectId) {
                    maxGameObjectId = gameObject.uid;
                }
            }
            Component.init(++maxComponentId);
            GameObject.init(++maxGameObjectId);

            isLevelLoaded = true;
        }
    }

    public GameObject getGameObjectByName(String name) {
        for (GameObject gameObject : gameObjects) {
            if (name.equals(gameObject.name)) {
                return gameObject;
            }
        }

        return null;
    }

    //TODO: 13.07.2024 возможно добавить в Gameobject переопределённый equals, который сравнивает только id, и вместо листа всех игровых объектов на сцене использовать хешсет либо хешмапу<id, Gameobject>, для ускоренного поиска
    public GameObject getGameObjectById(int id) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.uid == id) {
                return gameObject;
            }
        }

        return null;
    }

    //TODO: есть подозрение что этот метод жрёт дохрена ресурсов, нужно протестить производительность
    //TODO: идея: добавить отдельный метод, который будет искать все объекты, которые также рендерятся в picking шейдере. Нужно просмотреть все пиксели в нужном радиусе, и вернуть все найденные id'шники
    public List<GameObject> getAllGameObjectsInRadius(Vector2f origin, float radius) {
        List<GameObject> resultList = new ArrayList<>();

        for (GameObject go : gameObjects) {
            // TODO: смотреть на squared параметр
            if (go.transform.position.distance(origin) <= radius) {
                resultList.add(go);
            }
        }

        return resultList;
    }

    public void destroy() {
        for (GameObject go : gameObjects) {
            go.destroy();
        }
    }

    public static Scene getSceneById(int sceneId) {
        for (Scene scene : scenesList) {
            if (scene.uid == sceneId) {
                return scene;
            }
        }

        throw new RuntimeException("Scene with id=" + sceneId + " does not exist");
    }
}