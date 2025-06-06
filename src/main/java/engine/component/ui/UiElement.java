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
    public Vector4f screenPos = new Vector4f(-1, -1, 1, 1);

    //TODO: implement
    public Alignment alignment = Alignment.CENTER;

    public Vector4f color = new Vector4f(1);

    public List<UiSprite> uiSprites = new ArrayList<>();

    public List<UiText> uiTextBlocks = new ArrayList<>();

    public boolean keepProportions = true;

    public boolean isClickable = false;

    public Canvas canvas;

    public boolean isEnabled = false;

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
        float aspectRatio = Window.aspectRatio;

        float currentWidth = screenPos.z - screenPos.x;
        float currentHeight = screenPos.w - screenPos.y;
        float desiredNormalizedRatio = widthToHeightCoefficient / aspectRatio;
        float centerX = (screenPos.x + screenPos.z) * 0.5f;
        float centerY = (screenPos.y + screenPos.w) * 0.5f;

        if (currentWidth / currentHeight > desiredNormalizedRatio) {
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
}
