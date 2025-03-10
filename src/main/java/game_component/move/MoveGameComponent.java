package game_component.move;

import game_component.GameComponent;
import org.joml.Vector2f;

public abstract class MoveGameComponent extends GameComponent {

    public abstract void executeMovement(int button, Vector2f position);
}
