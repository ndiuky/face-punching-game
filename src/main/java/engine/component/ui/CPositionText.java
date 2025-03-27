package engine.component.ui;

import engine.component.Component;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.Color;

public class CPositionText extends Component {

    private CTextRenderer textRenderer;

    @Override
    public void start() {
        textRenderer = new CTextRenderer().setText(new Text("").setColor(Color.GREY3).setAlignment(Alignment.UPPER_MIDDLE));
        gameObject.addComponent(textRenderer);
    }

    @Override
    public void update(float dt) {
        textRenderer.setTextContent((int) gameObject.transform.position.x + ", " + (int) gameObject.transform.position.y);
//        gameObject.transform.position = new Vector2f(gameObject.transform.position);
    }
}
