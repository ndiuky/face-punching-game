package engine.object;


import engine.component.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int idCounter = 0;

    private int uid = -1;

    private String name;

    private List<Component> components;

    public Transform transform;

    private int zIndex;

    private boolean doSerialize = true;

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = transform;
        this.zIndex = zIndex;
        components = new ArrayList<>();

        uid = idCounter++;
    }

    public static void init(int maxId){
        idCounter = maxId;
    }

    public void start() {
        for (Component component : components) {
            component.start();
        }
    }

    public void update(float dt) {
        for (Component component : components) {
            component.update(dt);
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error: Casting component");
                }
            }
        }

        return null;
    }

    public GameObject addComponent(Component c) {
        c.generateId();
        components.add(c);
        c.gameObject = this;
        return this;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i=0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public List<Component> getAllComponents() {
        return components;
    }

    public int getZIndex() {
        return zIndex;
    }

    public int getUid() {
        return uid;
    }

    public boolean isDoSerialize() {
        return doSerialize;
    }

    public void setDoSerialize(boolean doSerialize) {
        this.doSerialize = doSerialize;
    }
}