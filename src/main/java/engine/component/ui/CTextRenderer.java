package engine.component.ui;

import engine.component.Component;
import engine.object.Transform;
import engine.render.Window;
import engine.text.Text;
import engine.util.Alignment;

//TODO: добавить флаг, отвечающий за влияние размера родительского объекта на размер текста
public class CTextRenderer extends Component {

    private Text text;

    private Transform lastTransform;

    public boolean isDirty = true;

    public CTextRenderer(final Text text) {
        this.text = text;
    }

    @Override
    public void start() {
        lastTransform = gameObject.transform.copy();
        if (text == null) {
            text = new Text("");
        }
        text.fillInMetadata();
        text.updatePositions();
    }

    @Override
    public void update(float dt) {
        if (text.textContent.isEmpty()) {
            return;
        }

        if (!lastTransform.equals(gameObject.transform)) {
            gameObject.transform.copy(lastTransform);
            isDirty = true;
        }
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
        text.alignment = alignment;

        if (Window.currentScene.isRunning) {
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
        return text.textContent;
    }
}
