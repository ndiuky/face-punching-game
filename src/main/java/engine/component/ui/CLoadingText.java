package engine.component.ui;

import engine.component.Component;
import engine.text.Text;
import engine.util.Alignment;
import util.TextAnimation;

public class CLoadingText extends Component {

    private CTextRenderer textRenderer;

    private float timer = 0;

    private int dots = 0;

    @Override
    public void start() {
        textRenderer = new CTextRenderer().setText(new Text("loading").setAlignment(Alignment.MIDDLE_LEFT).setAnimation(TextAnimation.SIZE_WAVING_FORWARDS));
        gameObject.addComponent(textRenderer);

        dots += getUid();
    }

    @Override
    public void update(float dt) {
        if (timer > 0.1f) {
            if (++dots >= 31) {
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
                case 16 -> textRenderer.setTextContent("loading..............");
                case 17 -> textRenderer.setTextContent("loading.............");
                case 18 -> textRenderer.setTextContent("loading............");
                case 19 -> textRenderer.setTextContent("loading...........");
                case 20 -> textRenderer.setTextContent("loading..........");
                case 21 -> textRenderer.setTextContent("loading.........");
                case 22 -> textRenderer.setTextContent("loading........");
                case 23 -> textRenderer.setTextContent("loading.......");
                case 24 -> textRenderer.setTextContent("loading......");
                case 25 -> textRenderer.setTextContent("loading.....");
                case 26 -> textRenderer.setTextContent("loading....");
                case 27 -> textRenderer.setTextContent("loading...");
                case 28 -> textRenderer.setTextContent("loading..");
                case 29 -> textRenderer.setTextContent("loading.");
                case 30 -> textRenderer.setTextContent("loading");
            }
        }

        timer += dt;
    }
}
