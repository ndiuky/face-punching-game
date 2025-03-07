package gameComponent;

import controllerAttiribute.ActionType;
import сomponent.entity.Entity;

public abstract class GameComponent {

    protected Entity entity;

    protected ActionType actionType;

    //If "isPassive" parameter is set to true, then this GameComponent should be updated every frame.
    //Otherwise, it should start executing on certain events
    protected boolean isPassive;

    public boolean getIsPassive(){
        return isPassive;
    }

    public abstract void init(Entity entity);

    public abstract void execute();
}
