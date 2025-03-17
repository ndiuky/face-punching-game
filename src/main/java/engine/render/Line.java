package engine.render;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Line {

    private Vector2f from;

    private Vector2f to;

    private int zOrder;

    private Vector4f color;

    //TODO: 22.06.2024 переделать lifetime на float для использования в секундах
    private int lifetime;

    public Line(Vector2f from, Vector2f to, int zOrder, Vector4f color, int lifetime) {
        this.from = from;
        this.to = to;
        this.zOrder = zOrder;
        this.color = color;
        this.lifetime = lifetime;
    }

    public int beginFrame() {
        return --lifetime;
    }

    public Vector2f getFrom() {
        return from;
    }

    public Vector2f getTo() {
        return to;
    }

    public int getzOrder() {
        return zOrder;
    }

    public Vector4f getColor() {
        return color;
    }
}