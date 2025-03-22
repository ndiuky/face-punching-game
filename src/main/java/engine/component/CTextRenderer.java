package engine.component;

import engine.object.Transform;
import engine.render.DebugDraw;
import engine.text.Glyph;
import engine.text.Text;
import engine.util.Color;
import org.joml.Vector2f;

public class CTextRenderer extends Component {

    private Text text;

    private transient Transform lastTransform;

    private transient boolean isDirty = true;


    @Override
    public void start() {
        lastTransform = gameObject.transform.copy();
        if (text == null) {
            text = Text.builder().build();
        }
//        text.fillInMetadata(gameObject.transform.position.x, gameObject.transform.position.y);
        text.fillInMetadata();
    }

    @Override
    public void update(float dt) {
        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
        DebugDraw.drawCircle(new Vector2f(text.getProperties().getMiddleX() + gameObject.transform.position.x, text.getProperties().getMiddleY() + gameObject.transform.position.y), 5, 1, engine.util.Color.WHITE);
        DebugDraw.drawLine(gameObject.transform.position, text.getPositions().getFirst(), 1);

        for (int i = 0; i < text.getTextSize(); i++) {
            Glyph glyph = text.getContent().get(i);
            Vector2f pos = text.getPositions().get(i);
            DebugDraw.drawBox(new Vector2f(pos).add(new Vector2f(glyph.width/2f, glyph.height/2f)).add(gameObject.transform.position), new Vector2f(glyph.width, glyph.height), 1, 0, Color.BLUE);
        }
    }

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
