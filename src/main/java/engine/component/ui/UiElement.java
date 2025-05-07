package engine.component.ui;

import engine.render.Window;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public abstract class UiElement {

    // (bottomLeftX, bottomLeftY, topRightX, topRightY)
    protected Vector4f screenPos = new Vector4f(-1, -1, 1, 1);

    protected Vector4f color = new Vector4f(1);

    protected boolean keepProportions = true;

    protected boolean isClickable = false;

    protected Canvas canvas;

    protected List<UiSprite> uiSprites = new ArrayList<>();

    public UiElement(Canvas canvas) {
        this.canvas = canvas;
    }

    public abstract void update();

    public Vector4f getScreenPos() {
        return new Vector4f(screenPos);
    }

    public UiElement setScreenPos(Vector4f screenPos) {
        this.screenPos = screenPos;

        return this;
    }

    public Vector4f getColor() {
        return new Vector4f(color);
    }

    public UiElement setColor(Vector4f color) {
        this.color = color;

        return this;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public UiElement setClickable(boolean clickable) {
        isClickable = clickable;

        return this;
    }

    public boolean isKeepProportions() {
        return keepProportions;
    }

    public UiElement setKeepProportions(boolean keepProportions) {
        this.keepProportions = keepProportions;

        return this;
    }

    //TODO: возможно не работает при изменённом screenPos
    public Vector4f getScreenPosWithKeptProportions(float widthToHeightCoefficient) {
        float aspectRatio = Window.getAspectRatio();

        if (widthToHeightCoefficient < aspectRatio) {
            float coefficient = widthToHeightCoefficient / aspectRatio;

            return new Vector4f(
                    screenPos.x * coefficient,
                    screenPos.y,
                    screenPos.z * coefficient,
                    screenPos.w
            );
        }

        float coefficient = aspectRatio / widthToHeightCoefficient;

        return new Vector4f(
                screenPos.x,
                screenPos.y * coefficient,
                screenPos.z,
                screenPos.w * coefficient
        );
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public List<UiSprite> getUiSprites() {
        return List.copyOf(uiSprites);
    }
}
