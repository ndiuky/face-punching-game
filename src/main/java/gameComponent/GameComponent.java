package gameComponent;

import component.entity.Bot;

@Deprecated
public abstract class GameComponent {

    protected Bot bot;

    public void init(Bot bot){
        this.bot = bot;
    }

    public void update(float dt) {
        //Override on demand
    }

    public abstract void stopExecution();
}
