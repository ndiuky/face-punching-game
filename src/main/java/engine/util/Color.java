package engine.util;

import org.joml.Vector4f;

public enum Color {
    TRANSPARENT(0, 0, 0, 0),
    WHITE(1, 1, 1, 1),
    RED(1, 0, 0, 1),
    GREEN(0, 1, 0, 1),
    BLUE(0, 0, 1, 1),
    BLACK(0, 0, 0, 1),
    GREY(0.3f, 0.3f, 0.3f, 1);

    public float r, g, b, a;

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