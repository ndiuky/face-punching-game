package engine.text;

import engine.component.ui.UIElement;
import engine.util.Alignment;
import engine.util.Color;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Text implements UIElement<Text> {

    private final List<Glyph> content = new ArrayList<>();

    private final List<Vector2f> positions = new ArrayList<>();

    private String textContent;

    //TODO: нигде не используется, добавить
    private float sizeModifier;

    private Color color;

    private float width, height;

    private Alignment alignment = Alignment.BOTTOM_LEFT;

    private Text(String textContent, float sizeModifier, Color color) {
        this.textContent = textContent;
        this.sizeModifier = sizeModifier;
        this.color = color;
    }

    public Text(String textContent) {
        this(textContent, 1, Color.WHITE);
    }

    public Text() {
    }

    public void fillInMetadata() {
        content.clear();
        positions.clear();

        Vector2f dimensions = Font.fillInTextMetadata(content, positions, textContent);
        width = dimensions.x;
        height = dimensions.y;
    }

    public Text setColor(Color color) {
        this.color = color;
        return this;
    }

    public int getTextSize() {
        return textContent.length();
    }

    public List<Glyph> getContent() {
        return content;
    }

    public List<Vector2f> getPositions() {
        return positions;
    }

    public void setSizeModifier(float sizeModifier) {
        this.sizeModifier = sizeModifier;
    }

    public float getSizeModifier() {
        return sizeModifier;
    }

    public Color getColor() {
        return color;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        if (Objects.equals(this.textContent, textContent)) {
            return;
        }

//        float previousWidth = width;
//        float previousHeight = height;

        this.textContent = textContent;
        fillInMetadata();

        //TODO: пофикси
//        if (previousWidth != width || previousHeight != height) {
        updateAlignment();
//        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    @Override
    public Alignment getAlignment() {
        return alignment;
    }

    @Override
    public Text setAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    @Override
    public void updateAlignment() {
        positions.clear();

        float totalX = alignment.x * width;
        float y = alignment.y * height;
        for (Glyph glyph : content) {
            positions.add(new Vector2f(totalX, y));
            totalX += glyph.width;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Text text)) return false;
        return Float.compare(sizeModifier, text.sizeModifier) == 0 && Objects.equals(textContent, text.textContent) && color == text.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(textContent, sizeModifier, color);
    }
}
