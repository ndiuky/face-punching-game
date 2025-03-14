package engine.component;

import engine.object.Transform;
import engine.text.Text;
import engine.util.Color;

public class CTextRenderer extends Component {

    private Color color = Color.WHITE;

    private Text text = new Text();

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

    public CTextRenderer setColor(Color color) {
        if (!this.color.equals(color)) {
            this.color = color;
            isDirty = true;
        }
        return this;
    }

//    public Texture getTexture() {
//        return sprite.getTexture();
//    }
//
//    public CTextRenderer setTexture(Texture texture){
//        sprite.setTexture(texture);
//        return this;
//    }
//
//    public Vector2f[] getTextureCoords() {
//        return sprite.getTexCoords();
//    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public CTextRenderer setIsDirty() {
        isDirty = false;
        return this;
    }

    public CTextRenderer setText(Text text) {
        this.text = text;
        isDirty = true;
        return this;
    }

    public Text getText() {
        return text;
    }
}
