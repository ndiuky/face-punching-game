package engine.component.ui;

import engine.text.Text;

public class UiText extends UiElement {

    public Text text;

    public UiText(Canvas canvas, Text text) {
        super(canvas);

        this.text = text;
        text.fillInMetadata();
        uiTextBlocks.add(this);
    }

    @Override
    protected void update() {

    }
}
