package engine.component.ui;

import engine.component.Component;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.Color;
import org.joml.Vector2f;

public class CPositionText extends Component {

    private CTextRenderer textRenderer;

    @Override
    public void start() {
        Text text = new Text("", 0.5f, Color.BLUE);
        text.alignment = Alignment.BOTTOM_CENTER;
        text.offset = new Vector2f(0, -0.75f);
        textRenderer = new CTextRenderer(text);
        gameObject.addComponent(textRenderer);
    }

    @Override
    public void update(float dt) {
        textRenderer.setTextContent((int) (gameObject.transform.position.x * 10) / 10f + ", " + (int) (gameObject.transform.position.y * 10) / 10f);
    }
}
