package component.entity;

import engine.component.CItemPickUp;
import engine.component.CSpriteRenderer;
import engine.component.Component;
import engine.event.KeyboardEventListener;
import engine.event.MouseEventListener;
import engine.object.Sprite;
import engine.util.AssetPool;
import gameComponent.CFistAttackRandom;
import gameComponent.CMoveWasd;

import java.util.List;

import static engine.event.MouseEventProducer.listenToMousePress;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class Player extends Bot implements MouseEventListener, KeyboardEventListener {

    private static final List<Component> REQUIRED_COMPONENTS = List.of(new CMoveWasd(),
            new CFistAttackRandom(),
            new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/dummy.png"))),
            new CItemPickUp());

    @Override
    public void start() {
        for (Component requiredComponent : REQUIRED_COMPONENTS) {
            if (gameObject.getComponents(requiredComponent.getClass()) == null) {
                gameObject.addComponent(requiredComponent);
            }
        }

        listenToMousePress(GLFW_MOUSE_BUTTON_1, this);
        listenToMousePress(GLFW_MOUSE_BUTTON_2, this);
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
    }

    @Override
    public void onKeyRelease(int key) {
    }
}
