package engine.component;

import engine.object.GameObject;

// TODO: реализовать isActive
public abstract class Component {

    private static int idCounter = 0;

    public int uid = -1;

    public GameObject gameObject = null;

    public static void init(int maxId) {
        idCounter = maxId;
    }

    public void start() {

    }

    public void update(float dt) {

    }

    public void generateId() {
        if (uid == -1) {
            uid = idCounter++;
        }
    }

    public void destroy() {

    }
}
