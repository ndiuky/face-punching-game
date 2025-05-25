package engine.component;

import engine.object.Sprite;
import engine.object.Transform;
import engine.render.Texture;
import engine.util.AssetPool;
import engine.util.Color;
import org.joml.Vector2f;
import org.joml.Vector4f;
import registry.TextureType;

public class CSpriteRenderer extends Component {

    private Vector4f color = Color.WHITE.asVector();

    private Sprite sprite;

    private Transform lastTransform;

    public boolean isDirty = true;

    public CSpriteRenderer(final Sprite sprite) {
        this.sprite = sprite;
    }

    public CSpriteRenderer(final TextureType texture) {
        sprite = new Sprite(AssetPool.getTexture(texture));
    }

    @Override
    public void start() {
        lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
    }

    public Vector4f getColor() {
        return color;
    }

    public CSpriteRenderer setColor(Color color) {
        return setColor(color.asVector());
    }

    public CSpriteRenderer setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.color = color;
            isDirty = true;
        }

        return this;
    }

    public Texture getTexture() {
        return sprite.texture;
    }

    public CSpriteRenderer setTexture(Texture texture) {
        sprite.texture = texture;

        return this;
    }

    public Vector2f[] getTextureCoords() {
        return sprite.texCoords;
    }

    public CSpriteRenderer setSprite(Sprite sprite) {
        this.sprite = sprite;
        isDirty = true;

        return this;
    }

    public Vector2f getAnchorPoint() {
        return sprite.anchorPoint;
    }

    public CSpriteRenderer setAnchorPoint(float x, float y) {
        sprite.anchorPoint = new Vector2f(x, y);

        return this;
    }
}