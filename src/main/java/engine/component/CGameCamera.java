package engine.component;

import engine.event.MouseEventListener;
import engine.render.Camera;
import engine.util.listener.KeyListener;
import engine.util.listener.MouseListener;
import org.joml.Vector2f;

import static engine.event.MouseEventProducer.listenToMousePress;
import static engine.event.MouseEventProducer.listenToMouseRelease;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class CGameCamera extends Component implements MouseEventListener {

    private float dragDebounce = 0.032f;
    private final Camera camera;
    private Vector2f clickOrigin;
    private boolean reset = false;
    private float lerpTime = 0.0f;
    private final float dragSensitivity = 30.0f;
    private final float scrollSensitivity = 0.1f;
    private boolean isDragging = false;

    public CGameCamera(Camera camera) {
        this.camera = camera;
        this.clickOrigin = new Vector2f();

        listenToMousePress(GLFW_MOUSE_BUTTON_MIDDLE, this);
        listenToMouseRelease(GLFW_MOUSE_BUTTON_MIDDLE, this);
    }

    @Override
    public void update(float dt) {

        if (isDragging) {
            if (dragDebounce > 0) {
                clickOrigin = MouseListener.getOrtho();
                dragDebounce -= dt;

                return;
            }

            Vector2f mousePos = MouseListener.getOrtho();
            camera.position.sub(new Vector2f(mousePos).sub(clickOrigin).mul(dt).mul(dragSensitivity));
            clickOrigin.lerp(mousePos, dt);
        } else if (dragDebounce <= 0.0f) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            camera.addZoom((float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / camera.getZoom()) * -Math.signum(MouseListener.getScrollY()));
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_COMMA)) {
            reset = true;
        }

        if (reset) {
            camera.position.lerp(new Vector2f(), lerpTime);
            camera.setZoom(this.camera.getZoom() + ((1.0f - camera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;

            if (Math.abs(camera.position.x) <= 1.0f &&
                    Math.abs(camera.position.y) <= 1.0f &&
                    Math.abs(camera.getZoom() - 1) <= 0.01f) {
                lerpTime = 0.0f;
                camera.position.set(0f, 0f);
                camera.setZoom(1.0f);
                reset = false;
            }
        }
    }

    @Override
    public void onMousePress(final int button) {
        isDragging = true;
    }

    @Override
    public void onMouseRelease(final int button) {
        isDragging = false;
    }
}
