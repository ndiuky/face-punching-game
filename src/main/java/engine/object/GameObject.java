package engine.object;


import engine.component.Component;
import engine.component.ui.CTextRenderer;
import engine.render.Window;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.Color;
import engine.util.Interactable;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private static int idCounter = 0;

    private int uid = -1;

    private String name;

    private final List<Component> components = new ArrayList<>();

    public Transform transform;

    private int zIndex;

    private boolean isDead = false;

    private boolean doSerialize = true;

    private boolean isInteractable;

    public GameObject(String name, Transform transform, int zIndex) {
        this.name = name;
        this.transform = transform;
        this.zIndex = zIndex;

        CTextRenderer textRenderer = new CTextRenderer().setText(new Text(name).setColor(Color.TRANSLUCENT)).setAlignment(Alignment.BOTTOM_MIDDLE);
        textRenderer.getText().setSizeModifier(0.5f);
        addComponent(textRenderer);

        uid = idCounter++;
    }

    public static void init(int maxId) {
        idCounter = maxId;
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

        if (Window.getCurrentScene().isLevelLoaded) {
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

    public String getName() {
        return name;
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public void setInteractable(boolean interactableByPlayer) {
        isInteractable = interactableByPlayer;
    }

    public boolean isDead() {
        return isDead;
    }
}