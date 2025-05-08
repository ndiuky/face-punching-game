package component.entity;

import component.ItemDrop;
import engine.component.CFistAttackRandom;
import engine.component.CInventory;
import engine.component.CItemPickUp;
import engine.component.CMoveWasd;
import engine.component.CSpriteRenderer;
import engine.component.Component;
import engine.component.ui.Canvas;
import engine.component.ui.UiTotalItems;
import engine.event.KeyboardEventListener;
import engine.event.MouseEventListener;
import engine.render.Window;
import engine.util.listener.MouseListener;
import org.joml.Vector2f;
import registry.ItemType;
import registry.TextureType;
import util.ItemStack;

import java.util.List;

import static engine.event.KeyboardEventProducer.listenToKeyboardPress;
import static engine.event.MouseEventProducer.listenToMousePress;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class Player extends Entity implements MouseEventListener, KeyboardEventListener {

    private CInventory inventory = new CInventory();

    private static final float ITEM_DROP_STRENGTH = 3.5f;

    private final Canvas canvas = new Canvas();

    private final List<Component> REQUIRED_COMPONENTS = List.of(new CMoveWasd(),
            new CFistAttackRandom(),
            new CSpriteRenderer(TextureType.DUMMY),
            new CItemPickUp(),
            inventory,
            canvas

    );

    @Override
    public void start() {
        for (Component requiredComponent : REQUIRED_COMPONENTS) {
            if (gameObject.getComponents(requiredComponent.getClass()) == null) {
                gameObject.addComponent(requiredComponent);
            }
        }

        canvas.addUiElement(new UiTotalItems(canvas, inventory));
//        Window.getCurrentScene().addGameObjectToScene(new GameObject("player's canvas", new Transform(new Vector2f(0), new Vector2f(1)), 1).addComponent(canvas));

        listenToMousePress(GLFW_MOUSE_BUTTON_1, this);
        listenToMousePress(GLFW_MOUSE_BUTTON_2, this);
        listenToKeyboardPress(GLFW_KEY_Q, this);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void onMousePress(int button) {
    }

    @Override
    public void onMouseRelease(int button) {
    }

    @Override
    public void onKeyPress(int key) {
        if (key == GLFW_KEY_Q) {
            dropItems(ItemType.STONE_PEBBLE, 1);
        }
    }

    @Override
    public void onKeyRelease(int key) {
    }

    /**
     * @return true если удалось выбросить нужное количество предметов, в ином случае false
     */
    public boolean dropItems(ItemType item, int amount) {
        if (!inventory.removeItems(item, amount)) {
            return false;
        }

        ItemDrop itemDrop = ItemDrop.generateForItemStack(new ItemStack(item, amount)).setDroppedManually(true);
        itemDrop.gameObject.transform.position = new Vector2f(gameObject.transform.position);
        itemDrop.applyInitialImpulse(
                MouseListener.getOrtho().sub(new Vector2f(gameObject.transform.position)).normalize().mul(ITEM_DROP_STRENGTH)
        );
        Window.getCurrentScene().addGameObjectToScene(itemDrop.gameObject);

        return true;
    }
}
