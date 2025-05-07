package component.controls;

import engine.component.Component;
import engine.object.GameObject;
import engine.render.Window;
import engine.util.listener.KeyListener;
import scene.SceneSelectionScene;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class RingControls extends Component {

    private GameObject player;

    private GameObject boxingRing;

    @Override
    public void start() {
        player = Window.getCurrentScene().getGameObjectByName("player");

        if (player == null) {
            throw new RuntimeException("Player in boxing scene is null");
        }

        boxingRing = Window.getCurrentScene().getGameObjectByName("boxingRing");

        if (boxingRing == null) {
            throw new RuntimeException("Boxing ring in boxing scene is null");
        }
    }

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Window.changeScene(SceneSelectionScene.class);
        }

        //TODO: переделать на нормальные коллайдеры
        if (player.transform.position.x > boxingRing.transform.scale.x / 2.1f - player.transform.scale.x) {
            player.transform.position.x = boxingRing.transform.scale.x / 2.1f - player.transform.scale.x;
//            player.getComponent(Bot.class).stopExecution(ActionType.MOVE);
        }
        if (player.transform.position.y > boxingRing.transform.scale.y / 2.1f - player.transform.scale.y) {
            player.transform.position.y = boxingRing.transform.scale.y / 2.1f - player.transform.scale.y;
//            player.getComponent(Bot.class).stopExecution(ActionType.MOVE);
        }
        if (player.transform.position.x < -boxingRing.transform.scale.x / 2.1f + player.transform.scale.x) {
            player.transform.position.x = -boxingRing.transform.scale.x / 2.1f + player.transform.scale.x;
//            player.getComponent(Bot.class).stopExecution(ActionType.MOVE);
        }
        if (player.transform.position.y < -boxingRing.transform.scale.y / 2.1f + player.transform.scale.y) {
            player.transform.position.y = -boxingRing.transform.scale.y / 2.1f + player.transform.scale.y;
//            player.getComponent(Bot.class).stopExecution(ActionType.MOVE);
        }
    }
}
