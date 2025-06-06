package engine.text;

import engine.util.Alignment;
import engine.util.Color;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static engine.text.TextAnimation.NONE;
import static engine.util.Alignment.BOTTOM_LEFT;

public class Text {

    public final List<Glyph> content = new ArrayList<>();

    public final List<Vector2f> positions = new ArrayList<>();

    public Vector2f offset = new Vector2f(0);

    public String textContent;

    private float sizeModifier;

    // TODO: антиалиасинг криво работает с цветами. Возможно проблема связана с прозрачностью, которая на данный момент не настроена
    public Color color;

    public float width;

    public float height;

    public Alignment alignment = BOTTOM_LEFT;

    public TextAnimation animation = NONE;

    public Text(String textContent, float sizeModifier, Color color) {
        this.textContent = textContent;
        this.sizeModifier = sizeModifier;
        this.color = color;
    }

    public Text(String textContent) {
        this(textContent, 1, Color.WHITE);
    }

    // TODO: нужно как-то разделить логику этого метода, и логику обновления исключительно позиций. Сейчас довольно часто вызываются оба этих метода подряд, по сути перетирая работу друг друга
    public void fillInMetadata() {
        content.clear();
        positions.clear();

        Vector2f dimensions = Font.fillInTextMetadata(content, sizeModifier, positions, textContent);
        width = dimensions.x;
        height = dimensions.y;
    }

    public Text setSizeModifier(float sizeModifier) {
        this.sizeModifier = sizeModifier;

        updatePositions();
        return this;
    }

    public float getSizeModifier() {
        return sizeModifier;
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
        updatePositions();
//        }
    }

    public void updatePositions() {
        positions.clear();

        float totalX = alignment.x * width;
        float y = alignment.y * height * sizeModifier;
        for (Glyph glyph : content) {
            positions.add(new Vector2f(totalX, y));
            totalX += glyph.width * sizeModifier;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Text text)) {
            return false;
        }
        return Float.compare(sizeModifier, text.sizeModifier) == 0 && Objects.equals(textContent, text.textContent) && color == text.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(textContent, sizeModifier, color);
    }
}
