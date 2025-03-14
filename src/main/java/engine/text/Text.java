package engine.text;

import engine.util.Color;
import org.joml.Vector2f;

import java.util.LinkedList;
import java.util.List;

public class Text {

    //TODO: мб поменять на ArrayList
    private List<Glyph> content = new LinkedList<>();

    private List<Vector2f> positions = new LinkedList<>();

    private float sizeModifier;

    private Color color;

    public void addGlyph(Glyph glyph, Vector2f pos){
       content.add(glyph);
       positions.add(pos);
    }


    public void setColor(Color color) {
        this.color = color;
    }

    public int getTextSize(){
        return positions.size();
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
}
