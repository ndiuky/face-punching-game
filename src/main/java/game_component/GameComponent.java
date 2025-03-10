package game_component;

import component.entity.Bot;

public abstract class GameComponent {

    protected Bot bot;

    public void init(final Bot bot){
        this.bot = bot;
    }

    //Override on demand
    public void update(final float dt) {
    }

    public abstract void stopExecution();
}
