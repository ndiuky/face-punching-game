package engine.object;

import engine.render.Texture;
import org.joml.Vector2f;

public class Sprite {

    // from [-0.5; -0.5] to [0.5; 0.5]
    public Vector2f anchorPoint = new Vector2f(0);

    public Vector2f[] texCoords = {
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    public Texture texture;

    public int height;

    public int width;

    public Sprite(Texture texture) {
        this.texture = texture;
    }

    public Sprite(Texture texture, Vector2f[] texCoords, int width, int height) {
        this.texture = texture;
        this.texCoords = texCoords;
        this.width = width;
        this.height = height;
    }
}