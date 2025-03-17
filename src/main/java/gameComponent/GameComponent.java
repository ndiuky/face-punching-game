package gameComponent;

import component.entity.Bot;

public abstract class GameComponent {

    protected Bot bot;

    public void init(final Bot bot){
        this.bot = bot;
    }

    public void update(final float dt) {
        //Override on demand
    }

    public abstract void stopExecution();
}
