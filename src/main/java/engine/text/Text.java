package engine.text;

import engine.util.Color;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Text {

    private final List<Glyph> content = new ArrayList<>();

    private final List<Vector2f> positions = new ArrayList<>();

    private TextProperties properties;

    private String textContent;

    //TODO: нигде не используется, добавить
    private float sizeModifier;

    private Color color;

    //TODO: переделать lifetime на float для использования в секундах
    //TODO: прикрутить lifetime к системе рендера, сейчас он всегда изначально равен 1
    private int lifetime = 1;

    private Text(String textContent, float sizeModifier, Color color, int lifetime) {
        this.textContent = textContent;
        this.sizeModifier = sizeModifier;
        this.color = color;
        this.lifetime = lifetime;
    }

    public void fillInMetadata() {
        properties = Font.fillInTextMetadata(content, positions, textContent);
    }

    public static TextBuilder builder() {
        return new TextBuilder();
    }

    public void setColor(Color color) {
        this.color = color;
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
        this.textContent = textContent;
    }

    public TextProperties getProperties() {
        return properties;
    }

    public int beginFrame() {
        return --lifetime;
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

    public static class TextProperties{

        private float width, height;

        private float middleX, middleY;

        public TextProperties(float width, float height, float middleX, float middleY) {
            this.width = width;
            this.height = height;
            this.middleX = middleX;
            this.middleY = middleY;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public float getMiddleX() {
            return middleX;
        }

        public float getMiddleY() {
            return middleY;
        }
    }

    public static class TextBuilder {

        private String textContent = "";

        private float xPos, yPos;

        private float sizeModifier = 1;

        private Color color = Color.WHITE;

        private int lifetime = 1;


        public Text build() {
            return new Text(textContent, sizeModifier, color, lifetime);
        }

        public TextBuilder textContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        public TextBuilder position(float x, float y) {
            this.xPos = x;
            this.yPos = y;
            return this;
        }

        public TextBuilder sizeModifier(float sizeModifier) {
            this.sizeModifier = sizeModifier;
            return this;
        }

        public TextBuilder color(Color color) {
            this.color = color;
            return this;
        }

        public TextBuilder lifetime(int lifetime) {
            this.lifetime = lifetime;
            return this;
        }

    }
}
