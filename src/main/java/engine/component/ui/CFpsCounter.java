package engine.component.ui;

import engine.component.Component;
import engine.text.Text;

import java.util.LinkedList;
import java.util.List;

public class CFpsCounter extends Component {

    private static final String FPS_TEXT_TEMPLATE = "%sfps: %s";

    private CTextRenderer textRenderer;

    private DisplayMode displayMode = DisplayMode.EVERY_SECOND_AVG;

    private float timer = 0;

    final private List<Integer> values = new LinkedList<>();

    @Override
    public void start() {
        textRenderer = new CTextRenderer().setText(new Text("calculating..."));
        gameObject.addComponent(textRenderer);
    }

    @Override
    public void update(float dt) {
        switch (displayMode) {
            case EVERY_FRAME -> {
                setNewText((int) (1 / dt));
            }
            case EVERY_SECOND_AVG -> {
                timer += dt;
                values.add((int) (1 / dt));
                if (timer >= 1) {
                    int sum = 0;
                    for (Integer value : values) {
                        sum += value;
                    }

                    setNewText(sum / values.size());
                    values.clear();

                    timer = 0;
                }
            }
            case EVERY_SECOND_MIN -> {
                timer += dt;
                values.add((int) (1 / dt));
                if (timer >= 1) {
                    int min = Integer.MAX_VALUE;
                    for (Integer value : values) {
                        if (value < min) {
                            min = value;
                        }
                    }

                    setNewText(min);
                    values.clear();

                    timer = 0;
                }
            }
            case EVERY_SECOND_MAX -> {
                timer += dt;
                values.add((int) (1 / dt));
                if (timer >= 1) {
                    int max = Integer.MIN_VALUE;
                    for (Integer value : values) {
                        if (value > max) {
                            max = value;
                        }
                    }

                    setNewText(max);
                    values.clear();

                    timer = 0;
                }
            }
        }
    }

    private void setNewText(Object value) {
        textRenderer.setTextContent(String.format(FPS_TEXT_TEMPLATE,
                switch (displayMode) {
                    case EVERY_SECOND_AVG -> "avg ";
                    case EVERY_SECOND_MIN -> "min ";
                    case EVERY_SECOND_MAX -> "max ";
                    default -> "";
                },
                value));
    }

    public CFpsCounter setDisplayMode(DisplayMode displayMode) {
        values.clear();
        this.displayMode = displayMode;
        return this;
    }

    public enum DisplayMode {
        EVERY_FRAME,
        EVERY_SECOND_AVG,
        EVERY_SECOND_MIN,
        EVERY_SECOND_MAX,

        ;
    }
}
