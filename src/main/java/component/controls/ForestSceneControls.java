package component.controls;

import engine.component.Component;
import engine.render.Window;
import engine.util.listener.KeyListener;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class ForestSceneControls extends Component {

    @Override
    public void update(float dt) {
        // TODO: переделать на ивент
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(Window.glfwWindow, true);
        }
    }
}
