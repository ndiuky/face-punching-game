package component.entity;

import engine.util.listener.InputEventListener;
import engine.util.listener.MouseListener;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

public class Player extends Bot implements InputEventListener {

    @Override
    public void start() {
        MouseListener.listenToPress(GLFW_MOUSE_BUTTON_1, this);
        MouseListener.listenToRelease(GLFW_MOUSE_BUTTON_2, this);

        gameComponents.forEach(gc -> gc.init(this));
    }

    @Override
    public void update(final float dt) {
        gameComponents.forEach(gameComponent -> gameComponent.update(dt));
    }

    @Override
    public void OneMousePress(final int button) {
        gameComponents.forEach(gc -> gc.onClick(button, MouseListener.getOrtho()));
    }

    @Override
    public void onMouseRelease(final int button) {
//        System.out.println("RELEASE " + button);
    }
}
