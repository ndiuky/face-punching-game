package engine.util;

public enum Alignment {
    // TODO: это полная хуйня, по умолчанию центровка должна быть по центру
    // По умолчанию текст центруется по левому нижнему углу, поэтому
    // все остальные выравнивания должны отталкиваться от этой позиции
    TOP_LEFT(0, -1), TOP_CENTER(-0.5f, -1), TOP_RIGHT(-1, -1),
    MIDDLE_LEFT(0, -0.5f), CENTER(-0.5f, -0.5f), MIDDLE_RIGHT(-1, -0.5f),
    BOTTOM_LEFT(0, 0), BOTTOM_CENTER(-0.5f, 0), BOTTOM_RIGHT(-1, 0);

    public final float x;

    public final float y;

    Alignment(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
