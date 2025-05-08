package engine.object;

import engine.component.CSpriteRenderer;
import org.joml.Vector2f;

public class Prefab {

    public static GameObject generateSpriteObject(Sprite sprite, float size, int zIndex) {
        GameObject generatedObject = new GameObject("Prefab",
                new Transform(new Vector2f(), new Vector2f(size)), zIndex);
        CSpriteRenderer renderer = new CSpriteRenderer(sprite);
        generatedObject.addComponent(renderer);

        return generatedObject;
    }

    public static GameObject generateSpriteObject(Sprite sprite, float size, int zIndex, String name) {
        GameObject generatedObject = new GameObject(name,
                new Transform(new Vector2f(), new Vector2f(size)), zIndex);
        CSpriteRenderer renderer = new CSpriteRenderer(sprite);
        generatedObject.addComponent(renderer);

        return generatedObject;
    }
}