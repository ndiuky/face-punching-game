package engine.text;

public class Glyph {

    public final int width, height;
    public final int x, y;
    public final float advance;

    public Glyph(int width, int height, int x, int y, float advance) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.advance = advance;
    }
}