package engine.component;

import engine.object.GameObject;

public abstract class Component {

    private static int idCounter = 0;

    private int uid = -1;

    public transient GameObject gameObject = null;

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

    public int getUid() {
        return uid;
    }
}
