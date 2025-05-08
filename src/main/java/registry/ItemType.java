package registry;

import engine.object.Sprite;
import engine.util.AssetPool;

public enum ItemType {

    NUT(TextureType.NUT_0, 0.4f),
    SCREW(TextureType.SCREW_0, 0.4f),
    // TODO: заменить на реальную текстуру
    STONE_PEBBLE(TextureType.STONE, 0.5f),
    CHIP(TextureType.CHIP_SMALL, 0.8f),
    ;

    public final Sprite sprite;
    public final float dropSizeMultiplier;

    ItemType(TextureType textureType, float dropSizeMultiplier) {
        sprite = new Sprite(AssetPool.getTexture(textureType));
        this.dropSizeMultiplier = dropSizeMultiplier;
    }
}
