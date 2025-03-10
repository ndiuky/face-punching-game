package game_component.block;

import game_component.GameComponent;

public abstract class BlockGameComponent extends GameComponent {

    public abstract void executeBlock(int button, float angle);
}
