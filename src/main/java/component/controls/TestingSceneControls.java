package component.controls;

import engine.component.Component;
import engine.render.Window;
import engine.util.listener.KeyListener;
import scene.SceneSelectionScene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class TestingSceneControls extends Component {

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.changeScene(SceneSelectionScene.class);
        }
    }
}
