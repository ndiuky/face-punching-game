package engine.object;

import engine.component.CSpriteRenderer;
import org.joml.Vector2f;

public class Prefab {

    public static GameObject generateSpriteObject(Sprite sprite, float sizeX, float sizeY, int zIndex) {
        GameObject block = new GameObject("Sprite_Object_Gen",
                new Transform(new Vector2f(), new Vector2f(sizeX, sizeY)), zIndex);
        CSpriteRenderer renderer = new CSpriteRenderer();
        renderer.setSprite(sprite);
        block.addComponent(renderer);

        return block;
    }
}