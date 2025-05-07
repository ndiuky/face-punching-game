package gameComponent;

import component.entity.Entity;

@Deprecated
public abstract class GameComponent {

    protected Entity entity;

    public void init(Entity entity){
        this.entity = entity;
    }

    public void update(float dt) {
        //Override on demand
    }

    public abstract void stopExecution();
}
