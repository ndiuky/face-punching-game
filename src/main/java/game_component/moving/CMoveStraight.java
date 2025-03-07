package game_component.moving;

import component.entity.Bot;
import controllerAttiribute.ActionType;
import game_component.GameComponent;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class CMoveStraight extends GameComponent {

    private Vector2f destination;

    private float movementSpeed = 400;

    @Override
    public void init(Bot bot) {
        actionType = ActionType.MOVE;
        this.bot = bot;
    }

    @Override
    public void update(final float dt) {
        if (destination != null) {
            goToTarget(dt);
        }
    }

    @Override
    public void onClick(final int button, final Vector2f clickPos) {
        if (button == GLFW_MOUSE_BUTTON_1){
            destination = clickPos;
        }
    }

    private void goToTarget(final float dt) {
        final Vector2f dir = new Vector2f(destination).sub(new Vector2f(bot.gameObject.transform.position));

        System.out.println("GOING TO " + (int) destination.x + ", " + (int) destination.y + ", curr pos " + (int) bot.gameObject.transform.position.x + ", " + (int) bot.gameObject.transform.position.y);

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(movementSpeed * dt).absolute().length()) {
            bot.gameObject.transform.position = destination;

            destination = null;
        } else {
            bot.gameObject.transform.position.add(new Vector2f(dir).normalize().mul(movementSpeed * dt));
        }
    }
}
