package component.controls;

import engine.component.Component;
import engine.render.Window;
import engine.util.listener.KeyListener;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class TestingSceneControls extends Component {

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void update(final float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.changeScene(2);
        }
    }
}
