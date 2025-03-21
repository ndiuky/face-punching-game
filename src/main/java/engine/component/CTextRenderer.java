package engine.component;

import engine.object.Transform;
import engine.text.Text;

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
