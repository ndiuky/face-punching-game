package game_component.attack;

import engine.object.GameObject;
import engine.object.Sprite;
import engine.util.AssetPool;
import game_component.GameComponent;

import static game_component.attack.AttackGameComponent.AttackState.INACTIVE;

public abstract class AttackGameComponent extends GameComponent {

    protected Sprite fistSprite = new Sprite().setTexture(AssetPool.getTexture("asset/texture/fist.png"));

    protected AttackState attackState = INACTIVE;

    public abstract void executeAttack(int button, GameObject target);

    protected enum AttackState{
        INACTIVE,
        MOVING_TO_TARGET,
        RETRACTING,

        ;
    }
}
