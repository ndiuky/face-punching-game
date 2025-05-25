package engine.render;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Line {

    public Vector4f color;

    public Vector2f from;

    public Vector2f to;

    public int zOrder;

    public Line(Vector2f from, Vector2f to, int zOrder, Vector4f color) {
        this.from = from;
        this.to = to;
        this.zOrder = zOrder;
        this.color = color;
    }
}