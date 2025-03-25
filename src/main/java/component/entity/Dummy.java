package component.entity;

public class Dummy extends Bot {

    @Override
    public void start() {
        gameObject.setInteractableByPlayer(true);

    }

    @Override
    public void update(float dt) {
        if (isKnocked) {
            flyFromKnock(dt);
        }
    }
}
