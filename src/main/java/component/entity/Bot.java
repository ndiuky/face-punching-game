package component.entity;

import controllerAttiribute.ActionType;
import controllerAttiribute.Knockable;
import engine.component.Component;
import game_component.GameComponent;
import game_component.attack.AttackGameComponent;
import game_component.block.BlockGameComponent;
import game_component.bonus.BonusGameComponent;
import game_component.move.MoveGameComponent;
import org.joml.Vector2f;

import java.util.function.Consumer;

public abstract class Bot extends Component implements Knockable {

    protected MoveGameComponent moveGC;

    protected AttackGameComponent attackGC;

    protected BlockGameComponent blockGC;

    protected BonusGameComponent bonusGC;

    //TODO: добавить коллайдер
    protected float knockbackResistance = 0;

    protected boolean isKnocked = false;


    public final Bot setActionGameComponent(MoveGameComponent moveGC) {
        this.moveGC = moveGC;
        return this;
    }

    public final Bot setActionGameComponent(AttackGameComponent attackGC) {
        this.attackGC = attackGC;
        return this;
    }

    public final Bot setActionGameComponent(BlockGameComponent blockGC) {
        this.blockGC = blockGC;
        return this;
    }

    public final Bot setActionGameComponent(BonusGameComponent bonusGC) {
        this.bonusGC = bonusGC;
        return this;
    }

    public void stopExecution(ActionType actionType) {
        switch (actionType) {
            case MOVE -> {
                if (moveGC != null) {
                    moveGC.stopExecution();
                }
            }
            case ATTACK -> {
                if (attackGC != null) {
                    attackGC.stopExecution();
                }
            }
            case BLOCK -> {
                if (blockGC != null) {
                    blockGC.stopExecution();
                }
            }
            case BONUS -> {
                if (bonusGC != null) {
                    bonusGC.stopExecution();
                }
            }
        }
    }

    public final void callForEachGameComponent(Consumer<GameComponent> consumer) {
        if (moveGC != null) {
            consumer.accept(moveGC);
        }
        if (attackGC != null) {
            consumer.accept(attackGC);
        }
        if (blockGC != null) {
            consumer.accept(blockGC);
        }
        if (bonusGC != null) {
            consumer.accept(bonusGC);
        }
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    @Override
    public void onKnockbackApplied(Vector2f incomingForce) {
        knockDir = new Vector2f(incomingForce).mul(1 - knockbackResistance);
        initialPos = new Vector2f(gameObject.transform.position);
        isKnocked = true;
        currFlyingTime = 0;
    }


    private final float flyingTime = 0.5f;
    private float currFlyingTime;
    private Vector2f knockDir;
    private Vector2f initialPos;

    protected void flyFromKnock(float dt) {

        final float factor = currFlyingTime >= flyingTime ? 1 : (float) (1 - Math.pow(2, -50 * currFlyingTime));

        gameObject.transform.position = new Vector2f(initialPos).add(new Vector2f(knockDir).mul(factor));

        currFlyingTime += dt;

        if (factor == 1) {
            isKnocked = false;
            currFlyingTime = 0;
        }
    }
}
