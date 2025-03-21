package engine.object;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.component.Component;
import engine.render.Camera;
import engine.render.Renderer;
import engine.util.serializer.ComponentSerializer;
import engine.util.serializer.GameObjectSerializer;

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

    protected Camera camera;

    private boolean isRunning = false;

    protected transient boolean isLevelLoaded;

    protected List<GameObject> gameObjects = new ArrayList<>();

    private transient Gson gson = new GsonBuilder()
            .registerTypeAdapter(Component.class, new ComponentSerializer())
            .registerTypeAdapter(GameObject.class, new GameObjectSerializer())
            .create();

    private static final List<Scene> scenesList = new ArrayList<>();

    public Scene() {
        uid = idCounter++;
        scenesList.add(this);
    }

    public void init() {

    }

    public void start() {
        for (GameObject go : gameObjects) {
            go.start();
            renderer.add(go);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject go) {
        if (!isRunning) {
            gameObjects.add(go);
        } else {
            gameObjects.add(go);
            go.start();
            renderer.add(go);
//            if (go.getComponent(CSpriteRenderer.class) != null){
//                renderer.add(go);
//            }
//            if (go.getComponent(CTextRenderer.class) != null){
//                TextRenderer.add();
//            }
        }
    }

    public final void update(float dt) {
        this.camera.adjustProjection();

        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if (go.isDead()) {
                gameObjects.remove(i--);
                renderer.destroyGameObject(go);
            }
        }
    }

    public void render() {
        renderer.render();
    }

    public Camera getCamera() {
        return camera;
    }

    public void saveSceneData() {
        try {
            FileWriter writer = new FileWriter(SAVE_FILE_NAME);
            List<GameObject> gameObjectsToSerialize = new ArrayList<>();
            for (GameObject gameObject : gameObjects) {
                if (gameObject.isDoSerialize()) {
                    gameObjectsToSerialize.add(gameObject);
                }
            }
            writer.write(gson.toJson(gameObjectsToSerialize));
            writer.close();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to save scene data: " + ex.getMessage());
        }
    }

    public void loadSceneData() {
        Path path = Paths.get(SAVE_FILE_NAME);
        if (!Files.exists(path)) return;

        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(path));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read bytes from save file: " + ex.getMessage());
        }

        if (!inFile.isEmpty()) {
            int maxComponentId = -1;
            int maxGameObjectId = -1;

            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);
            for (GameObject gameObject : objects) {
                addGameObjectToScene(gameObject);
                for (Component c : gameObject.getAllComponents()) {
                    if (c.getUid() > maxComponentId) {
                        maxComponentId = c.getUid();
                    }
                }
                if (gameObject.getUid() > maxGameObjectId) {
                    maxGameObjectId = gameObject.getUid();
                }
            }
            Component.init(++maxComponentId);
            GameObject.init(++maxGameObjectId);

            isLevelLoaded = true;
        }
    }

    public GameObject getGameObjectByName(String name) {
        for (GameObject gameObject : gameObjects) {
            if (name.equals(gameObject.getName())) {
                return gameObject;
            }
        }

        return null;
    }

    //TODO: 13.07.2024 возможно добавить в Gameobject переопределённый equals, который сравнивает только id, и вместо листа всех игровых объектов на сцене использовать хешсет либо хешмапу<id, Gameobject>, для ускоренного поиска
    public GameObject getGameObjectById(int id) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getUid() == id) {
                return gameObject;
            }
        }

        return null;
    }

    public void destroy() {
        for (GameObject go : gameObjects) {
            go.destroy();
        }
    }

    public int getUid() {
        return uid;
    }

    public List<Scene> getAllScenes() {
        return scenesList;
    }

    public static Scene getSceneById(int sceneId) {
        for (Scene scene : scenesList) {
            if (scene.getUid() == sceneId) {
                return scene;
            }
        }

        throw new RuntimeException("Scene with id=" + sceneId + " does not exist");
    }

}