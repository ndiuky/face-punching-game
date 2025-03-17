package engine.text;

import engine.util.Color;
import org.joml.Vector2f;

import java.util.LinkedList;
import java.util.List;

public class Text {

    //TODO: мб поменять на ArrayList
    private final List<Glyph> content = new LinkedList<>();

    private final List<Vector2f> positions = new LinkedList<>();

    private String textContent;

    private float sizeModifier;

    private Color color;

    //TODO: переделать lifetime на float для использования в секундах
    //TODO: прикрутить lifetime к системе рендера, сейчас он всегда изначально равен 1
    private int lifetime = 1;

    public void addGlyph(Glyph glyph, Vector2f pos){
       content.add(glyph);
       positions.add(pos);
    }


    public void setColor(Color color) {
        this.color = color;
    }

    public int getTextSize(){
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

    public int beginFrame() {
        return --lifetime;
    }
}
