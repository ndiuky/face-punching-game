package engine.component.ui;

import engine.component.Component;
import engine.text.Text;
import engine.util.Alignment;

public class CLoadingText extends Component {

    private CTextRenderer textRenderer;

    private float timer = 0;

    private int dots = 0;

    @Override
    public void start() {
        textRenderer = new CTextRenderer().setText(new Text("loading").setAlignment(Alignment.CENTER));
        gameObject.addComponent(textRenderer);

        dots += getUid();
    }

    @Override
    public void update(float dt) {
        if (timer > 0.05f) {
            if (++dots >= 16) {
                dots = 0;
            }
            timer = 0;

            switch (dots) {
                case 0 -> textRenderer.setTextContent("loading");
                case 1 -> textRenderer.setTextContent("loading.");
                case 2 -> textRenderer.setTextContent("loading..");
                case 3 -> textRenderer.setTextContent("loading...");
                case 4 -> textRenderer.setTextContent("loading....");
                case 5 -> textRenderer.setTextContent("loading.....");
                case 6 -> textRenderer.setTextContent("loading......");
                case 7 -> textRenderer.setTextContent("loading.......");
                case 8 -> textRenderer.setTextContent("loading........");
                case 9 -> textRenderer.setTextContent("loading.........");
                case 10 -> textRenderer.setTextContent("loading..........");
                case 11 -> textRenderer.setTextContent("loading...........");
                case 12 -> textRenderer.setTextContent("loading............");
                case 13 -> textRenderer.setTextContent("loading.............");
                case 14 -> textRenderer.setTextContent("loading..............");
                case 15 -> textRenderer.setTextContent("loading...............");
            }
        }

        timer += dt;
    }
}
