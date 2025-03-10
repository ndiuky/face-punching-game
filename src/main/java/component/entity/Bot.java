package component.entity;

import controllerAttiribute.ActionType;
import engine.component.Component;
import game_component.GameComponent;
import game_component.attack.AttackGameComponent;
import game_component.block.BlockGameComponent;
import game_component.bonus.BonusGameComponent;
import game_component.move.MoveGameComponent;

import java.util.function.Consumer;

public abstract class Bot extends Component {

    protected MoveGameComponent moveGC;

    protected AttackGameComponent attackGC;

    protected BlockGameComponent blockGC;

    protected BonusGameComponent bonusGC;

    //TODO: добавить коллайдер


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

    public void stopExecution(ActionType actionType){
        switch (actionType){
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

    protected final void callForEachGameComponent(Consumer<GameComponent> consumer) {
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

}
