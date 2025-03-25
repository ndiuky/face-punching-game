package engine.util;

import org.joml.Vector4f;

public enum Color {
    TRANSPARENT(0, 0, 0, 0),
    TRANSLUCENT(1, 1, 1, 0.5f),
    WHITE(1, 1, 1, 1),
    BLACK(0, 0, 0, 1),

    GREY1(0.1f, 0.1f, 0.1f, 1),
    GREY2(0.2f, 0.2f, 0.2f, 1),
    GREY3(0.3f, 0.3f, 0.3f, 1),
    GREY4(0.4f, 0.4f, 0.4f, 1),
    GREY5(0.5f, 0.5f, 0.5f, 1),
    GREY6(0.6f, 0.6f, 0.6f, 1),
    GREY7(0.7f, 0.7f, 0.7f, 1),
    GREY8(0.8f, 0.8f, 0.8f, 1),
    GREY9(0.9f, 0.9f, 0.9f, 1),

    RED(1, 0, 0, 1),
    GREEN(0, 1, 0, 1),
    BLUE(0, 0, 1, 1),

    YELLOW(1,1,0,1),
    MAGENTA(1,0,1,1),
    LIGHT_BLUE(0,1,1,1),

    ;

    public final float r, g, b, a;

    Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Vector4f asVector() {
        return new Vector4f(r, g, b, a);
    }
}