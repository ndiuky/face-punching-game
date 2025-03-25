package engine.component.ui;

import engine.component.Component;
import engine.object.Transform;
import engine.render.DebugDraw;
import engine.render.Window;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.Color;

public class CTextRenderer extends Component{

    private Text text;

    private transient Transform lastTransform;

    private transient boolean isDirty = true;

    @Override
    public void start() {
        lastTransform = gameObject.transform.copy();
        if (text == null) {
            text = new Text();
        }
        text.fillInMetadata();
        text.updateAlignment();
    }

    @Override
    public void update(float dt) {
        if (text.getTextSize() == 0) {
            return;
        }

        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }

//        DebugDraw.drawCircle(gameObject.transform.position, 10, 1, Color.RED);
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

    public CTextRenderer setTextContent(String textContent) {
        text.setTextContent(textContent);
        isDirty = true;
        return this;
    }

    public CTextRenderer setAlignment(Alignment alignment) {
        text.setAlignment(alignment);
        if (Window.getCurrentScene().isRunning()) {
            text.updateAlignment();
        }
        isDirty = true;
        return this;
    }

    public Text getText() {
        return text;
    }

    @Override
    public String toString() {
        return text.getTextContent();
    }
}
