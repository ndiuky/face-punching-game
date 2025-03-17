package gameComponent.attack;

import engine.object.GameObject;
import engine.object.Sprite;
import engine.util.AssetPool;
import gameComponent.GameComponent;

import static gameComponent.attack.AttackGameComponent.AttackState.INACTIVE;

public abstract class AttackGameComponent extends GameComponent {

    protected Sprite fistSprite = new Sprite().setTexture(AssetPool.getTexture("asset/texture/fist.png"));

    protected AttackState attackState = INACTIVE;

    protected GameObject target;

    public abstract void executeAttack(int button, GameObject target);

    protected enum AttackState{
        INACTIVE,
        MOVING_TO_TARGET,
        RETRACTING,

        ;
    }
}
