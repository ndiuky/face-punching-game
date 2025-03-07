package game_component;

import component.entity.Bot;
import controllerAttiribute.ActionType;
import org.joml.Vector2f;

public abstract class GameComponent {

    protected Bot bot;

    protected ActionType actionType;

    public abstract void init(final Bot bot);

    //Override on demand
    public void update(final float dt) {
    }

    //Override on demand
    public void onClick(final int button, final Vector2f clickPos) {
    }

    public void onRelease(final int button, final Vector2f releasePos) {
    }
}
