package gameComponent.block;

import gameComponent.GameComponent;

@Deprecated
public abstract class BlockGameComponent extends GameComponent {

    public abstract void executeBlock(int button, float angle);
}
