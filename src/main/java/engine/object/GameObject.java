package engine.object;


import engine.component.Component;
import engine.render.Window;
import engine.util.Interactable;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    public final List<Component> components = new ArrayList<>();

    private static int idCounter = 0;

    public boolean doSerialize = true;

    public boolean isDead = false;

    public boolean isInteractable;

    public Transform transform;

    public String name;

    public int uid = -1;

    public int zIndex;


    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = transform;
        this.zIndex = zIndex;

        uid = idCounter++;
    }

    public static void init(int maxId) {
        idCounter = maxId;
    }

    public static GameObject create() {
        return new GameObject("defaultName", new Transform(new Vector2f(), new Vector2f(1)), -1);
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            component.start();
        }
    }

    public void update(float dt) {
        for (Component component : components) {
            component.update(dt);
        }
    }

    public <T extends Component> List<T> getComponents(Class<T> componentClass) {
        List<T> resultingList = new ArrayList<>();
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    resultingList.add(componentClass.cast(c));
                } catch (ClassCastException e) {
                    throw new RuntimeException("Error casting component");
                }
            }
        }

        return resultingList.isEmpty() ? null : resultingList;
    }

    public GameObject addComponent(Component c) {
        c.generateId();
        components.add(c);
        c.gameObject = this;

        if (c instanceof Interactable) {
            isInteractable = true;
        }

        if (Window.currentScene.isLevelLoaded) {
            c.start();
        }

        return this;
    }

    //TODO: добавить сюда сброс isInteractable если удаляется соотв. компонент
    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void destroy() {
        isDead = true;
        for (Component curr : components) {
            curr.destroy();
        }
    }
}