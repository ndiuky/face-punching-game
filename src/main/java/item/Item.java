package item;

import engine.object.Sprite;
import engine.util.AssetPool;

public enum Item {

    NUT("nut-0", true),
    SCREW("screw-0", true),
    // TODO: заменить на реальную текстуру
    STONE_PEBBLE("stone", true),
    CHIP("chip-small", false),
    ;

    public final Sprite sprite;
    public final boolean isStackable;

    Item(String texturePath, boolean isStackable) {
        sprite = new Sprite().setTexture(AssetPool.getTexture(texturePath));
        this.isStackable = isStackable;
    }
}
