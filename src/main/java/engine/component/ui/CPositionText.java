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
        textRenderer = new CTextRenderer(
                new Text("")
                        .setColor(Color.BLUE)
                        .setSizeModifier(0.5f)
                        .setAlignment(Alignment.BOTTOM_CENTER)
                        .setOffset(new Vector2f(0, -0.75f)));
        gameObject.addComponent(textRenderer);
    }

    @Override
    public void update(float dt) {
        textRenderer.setTextContent((int) (gameObject.transform.position.x * 10) / 10f + ", " + (int) (gameObject.transform.position.y * 10) / 10f);
    }
}
