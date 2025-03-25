package gameComponent.move;

import gameComponent.GameComponent;
import org.joml.Vector2f;

@Deprecated
public abstract class MoveGameComponent extends GameComponent {

    public abstract void executeMovement(int button, Vector2f position);
}
