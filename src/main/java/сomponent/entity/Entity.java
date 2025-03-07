package сomponent.entity;

import engine.component.Component;
import gameComponent.GameComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity extends Component {

    protected List<GameComponent> gameComponents = new ArrayList<>();

//    protected Vector2f velocity = new Vector2f(0,0);

    //TODO: добавить коллайдер


    public Entity addGameComponent(GameComponent gameComponent) {
        gameComponents.add(gameComponent);
        return this;
    }


}
