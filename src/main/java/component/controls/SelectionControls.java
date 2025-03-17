package component.controls;

import engine.component.Component;
import engine.object.GameObject;
import engine.render.Window;
import engine.util.listener.KeyListener;
import engine.util.listener.MouseListener;
import scene.BoxingRingScene;
import scene.TestingScene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class SelectionControls extends Component {

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_BACKSPACE)) {
            System.out.println("exit");
            glfwSetWindowShouldClose(Window.getGlfwWindow(), true);
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_1)) {
            GameObject go;
            if ((go = MouseListener.getGameObjectUnderCursor()) != null) {
                if (go.getName().equals("ring select")) {
                    Window.changeScene(BoxingRingScene.class);
                } else if (go.getName().equals("testing select")) {
                    Window.changeScene(TestingScene.class);
                }
            }
        }
    }
}
