package engine.util;

public enum Alignment {
    UPPER_LEFT(0, -1), UPPER_MIDDLE(-0.5f, -1), UPPER_RIGHT(-1, -1),
    MIDDLE_LEFT(0, -0.5f), CENTER(-0.5f, -0.5f), MIDDLE_RIGHT(-1, -0.5f),
    BOTTOM_LEFT(0, 0), BOTTOM_MIDDLE(-0.5f, 0), BOTTOM_RIGHT(-1, 0);

    public final float x, y;

    Alignment(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
