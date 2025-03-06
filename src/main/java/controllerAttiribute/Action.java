package controllerAttiribute;

import org.joml.Vector2f;

public class Action {

    private ActionType type;

    private Vector2f targetPos;

    public void perform(){

    }

    public enum ActionType{

        MOVE,
        ATTACK,
        BLOCK,

        ;
    }
}
