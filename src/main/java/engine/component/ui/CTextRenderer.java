package engine.component.ui;

import engine.component.Component;
import engine.object.Transform;
import engine.render.Window;
import engine.text.Text;
import engine.util.Alignment;

public class CTextRenderer extends Component {

    private Text text;

    //TODO: добавить флаг, отвечающий за влияние размера родительского объекта на размер текста

    private Transform lastTransform;

    private boolean isDirty = true;

    public CTextRenderer(final Text text) {
        this.text = text;
    }

    @Override
    public void start() {
        lastTransform = gameObject.transform.copy();
        if (text == null) {
            text = new Text();
        }
        text.fillInMetadata();
        text.updatePositions();
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
    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public CTextRenderer setIsDirty() {
        isDirty = true;

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
            text.updatePositions();
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
