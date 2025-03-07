package component.entity;

import engine.component.Component;
import game_component.GameComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class Bot extends Component {

    protected List<GameComponent> gameComponents = new ArrayList<>();

    //TODO: добавить коллайдер

    public Bot addGameComponent(GameComponent gameComponent) {
        gameComponents.add(gameComponent);
        return this;
    }
}
