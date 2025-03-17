package engine.component;

import engine.object.Sprite;
import engine.object.Transform;
import engine.render.Texture;
import engine.util.Color;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Color color = Color.WHITE;

    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;

    private transient boolean isDirty = true;

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

    public Color getColor() {
        return color;
    }

    public SpriteRenderer setColor(Color color) {
        if (!this.color.equals(color)) {
            this.color = color;
            isDirty = true;
        }

        return this;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public SpriteRenderer setTexture(Texture texture){
        sprite.setTexture(texture);

        return this;
    }

    public Vector2f[] getTextureCoords() {
        return sprite.getTexCoords();
    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public SpriteRenderer setIsDirty() {
        isDirty = false;

        return this;
    }

    public SpriteRenderer setSprite(Sprite sprite) {
        this.sprite = sprite;
        isDirty = true;

        return this;
    }
}