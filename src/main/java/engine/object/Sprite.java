package engine.object;

import engine.render.Texture;
import engine.util.AssetPool;
import org.joml.Vector2f;

public class Sprite {

    private Texture texture = null;

    private int width;

    private int height;

    private Vector2f[] texCoords = {
            new Vector2f(1, 1),
            new Vector2f(1, 0),
            new Vector2f(0, 0),
            new Vector2f(0, 1)
    };

    // from [-0.5; -0.5] to [0.5; 0.5]
    private Vector2f anchorPoint = new Vector2f(0);

    public Sprite(final Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public Sprite setTexture(Texture texture) {
        this.texture = texture;

        return this;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    public Sprite setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public Sprite setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public Sprite setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getTexId() {
        return texture == null ? -1 : texture.getTexID();
    }

    public Vector2f getAnchorPoint() {
        return new Vector2f(anchorPoint);
    }

    public Sprite setAnchorPoint(Vector2f anchorPoint) {
        this.anchorPoint = anchorPoint;

        return this;
    }
}