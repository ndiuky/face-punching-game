package component.entity;

import component.ItemDrop;
import engine.component.CFistAttackRandom;
import engine.component.CInventory;
import engine.component.CItemPickUp;
import engine.component.CMoveWasd;
import engine.component.CSpriteRenderer;
import engine.component.Component;
import engine.component.ui.Canvas;
import engine.component.ui.UiInventory;
import engine.component.ui.UiTotalItems;
import engine.event.KeyboardEventListener;
import engine.event.MouseEventListener;
import engine.render.Window;
import engine.util.listener.MouseListener;
import org.joml.Math;
import org.joml.Vector2f;
import registry.ItemType;
import registry.TextureType;
import util.ItemStack;

import java.util.List;

import static engine.event.KeyboardEventProducer.listenToKeyboardPress;
import static engine.event.MouseEventProducer.listenToMousePress;
import static java.lang.Math.pow;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class Player extends Entity implements MouseEventListener, KeyboardEventListener {

    private static final float ITEM_DROP_STRENGTH = 3.5f;

    private static final float INVENTORY_ANIMATION_TIME = 0.2f;

    private static final float INVENTORY_ANIMATION_EASING = 1.70158f;

    private CInventory inventory = new CInventory();

    private final Canvas canvas = new Canvas();

    private final UiInventory uiInventory = new UiInventory(canvas, inventory);

    private InventoryOpeningState inventoryState = uiInventory.isEnabled() ? InventoryOpeningState.FULLY_OPENED : InventoryOpeningState.FULLY_CLOSED;

    private float currInventoryAnimationTime = 0;

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
        canvas.addUiElement(uiInventory);
//        Window.getCurrentScene().addGameObjectToScene(new GameObject("player's canvas", new Transform(new Vector2f(0), new Vector2f(1)), 1).addComponent(canvas));

        listenToMousePress(GLFW_MOUSE_BUTTON_1, this);
        listenToMousePress(GLFW_MOUSE_BUTTON_2, this);
        listenToKeyboardPress(GLFW_KEY_Q, this);
        listenToKeyboardPress(GLFW_KEY_E, this);
    }

    @Override
    public void update(float dt) {
        if (!inventoryState.isStableState) {
            playInventoryAnimation(dt);
        }
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
        } else if (key == GLFW_KEY_E) {
            switchInventoryOpenState();
        }
    }

    private void switchInventoryOpenState() {
        inventoryState = InventoryOpeningState.getOpposedStartingState(inventoryState);
        if (InventoryOpeningState.IS_OPENING.equals(inventoryState)) {
            uiInventory.setEnabled(true);
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


    private void playInventoryAnimation(float dt) {
        float resultingSize;

        float currProcessStep = Math.clamp(0, 1, currInventoryAnimationTime / INVENTORY_ANIMATION_TIME);
        // TODO: перенести эту и подобные интерполяции в утилитарный класс с анимациями
        float factor = Math.lerp(0, 1,
                (float) (1 + (INVENTORY_ANIMATION_EASING + 1) * pow(currProcessStep - 1, 3) + INVENTORY_ANIMATION_EASING * pow(currProcessStep - 1, 2)));
        if (InventoryOpeningState.IS_OPENING.equals(inventoryState)) {
            resultingSize = UiInventory.DEFAULT_RELATIVE_SCREEN_SIZE * factor;
        } else {
            resultingSize = UiInventory.DEFAULT_RELATIVE_SCREEN_SIZE * (1 - Math.min(factor, 1));
        }

        uiInventory.setRelativeScreenSize(resultingSize);
        if (currInventoryAnimationTime >= INVENTORY_ANIMATION_TIME) {
            inventoryState = InventoryOpeningState.getCompletedState(inventoryState);
            currInventoryAnimationTime = 0;
            uiInventory.setEnabled(InventoryOpeningState.isOpened(inventoryState));

            return;
        }

        currInventoryAnimationTime += dt;
    }

    private enum InventoryOpeningState {
        FULLY_OPENED(true),
        FULLY_CLOSED(true),
        IS_OPENING(false),
        IS_CLOSING(false),
        ;

        boolean isStableState;

        InventoryOpeningState(boolean isStableState) {
            this.isStableState = isStableState;
        }

        // TODO: переделать все методы под нестатичные
        public static InventoryOpeningState getOpposedStartingState(InventoryOpeningState state) {
            return switch (state) {
                case FULLY_CLOSED,
                     IS_CLOSING -> IS_OPENING;
                case FULLY_OPENED,
                     IS_OPENING -> IS_CLOSING;
            };
        }

        public static InventoryOpeningState getCompletedState(InventoryOpeningState state) {
            return switch (state) {
                case IS_CLOSING,
                     FULLY_CLOSED -> FULLY_CLOSED;
                case IS_OPENING,
                     FULLY_OPENED -> FULLY_OPENED;
            };
        }

        public static boolean isOpened(InventoryOpeningState state) {
            return switch (state) {
                case IS_CLOSING,
                     FULLY_CLOSED -> false;
                case IS_OPENING,
                     FULLY_OPENED -> true;
            };
        }
    }
}
