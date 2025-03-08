package component.entity;

public class Dummy extends Bot {

    @Override
    public void start() {
        gameComponents.forEach(gc -> gc.init(this));
    }

    @Override
    public void update(final float dt) {
        gameComponents.forEach(gameComponent -> gameComponent.update(dt));
    }
}
