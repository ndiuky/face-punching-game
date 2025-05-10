package engine.component.ui;

import engine.render.Window;
import engine.util.Alignment;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

//TODO: определять id UI объекта на который пришлось нажатие, опираясь на его screenPos
public abstract class UiElement {

    private static int idCounter = 0;

    private int uid = -1;

    // (bottomLeftX, bottomLeftY, topRightX, topRightY)
    protected Vector4f screenPos = new Vector4f(-1, -1, 1, 1);

    //TODO: implement
    protected Alignment alignment = Alignment.CENTER;

    protected Vector4f color = new Vector4f(1);

    protected List<UiSprite> uiSprites = new ArrayList<>();

    protected boolean keepProportions = true;

    protected boolean isClickable = false;

    protected Canvas canvas;

    protected boolean isEnabled = false;



    public UiElement(Canvas canvas) {
        this.canvas = canvas;

        uid = idCounter++;
    }

    public final void updateInternal() {
        if (isEnabled) {
            update();
        }
    }

    protected abstract void update();

    public Vector4f getScreenPosWithKeptProportions(float widthToHeightCoefficient) {
        float aspectRatio = Window.getAspectRatio();

        float currentWidth = screenPos.z - screenPos.x;
        float currentHeight = screenPos.w - screenPos.y;
        float desiredNormalizedRatio = widthToHeightCoefficient / aspectRatio;
        float currentNormalizedRatio = currentWidth / currentHeight;
        float centerX = (screenPos.x + screenPos.z) * 0.5f;
        float centerY = (screenPos.y + screenPos.w) * 0.5f;

        if (currentNormalizedRatio > desiredNormalizedRatio) {
            float newWidth = currentHeight * desiredNormalizedRatio * 0.5f;

            return new Vector4f(
                    centerX - newWidth,
                    screenPos.y,
                    centerX + newWidth,
                    screenPos.w
            );
        }

        float newHeight = currentWidth / desiredNormalizedRatio * 0.5f;

        return new Vector4f(
                screenPos.x,
                centerY - newHeight,
                screenPos.z,
                centerY + newHeight
        );
    }

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


    public Canvas getCanvas() {
        return canvas;
    }

    public List<UiSprite> getUiSprites() {
        return List.copyOf(uiSprites);
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public UiElement setAlignment(final Alignment alignment) {
        this.alignment = alignment;

        return this;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(final boolean enabled) {
        isEnabled = enabled;
    }

    public int getUid() {
        return uid;
    }
}
