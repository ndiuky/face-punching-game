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

public class CDraggingCamera extends Component implements MouseEventListener {

    private float dragDebounce = 0.032f;
    private Camera camera;
    private Vector2f clickOrigin;
    private boolean reset = false;
    private float lerpTime = 0.0f;
    private final float dragSensitivity = 30.0f;
    private final float scrollSensitivity = 0.1f;
    private boolean isDragging = false;

    public CDraggingCamera(Camera camera) {
        this.camera = camera;
        this.clickOrigin = new Vector2f();

        listenToMousePress(GLFW_MOUSE_BUTTON_MIDDLE, this);
        listenToMouseRelease(GLFW_MOUSE_BUTTON_MIDDLE, this);
    }

    @Override
    public void update(float dt) {

        if (isDragging) {
            if (dragDebounce > 0) {
                clickOrigin = new Vector2f(MouseListener.getOrtho());
                dragDebounce -= dt;

                return;
            }

            Vector2f mousePos = MouseListener.getOrtho();
            camera.position.sub(new Vector2f(mousePos).sub(clickOrigin).mul(dt).mul(dragSensitivity));
            clickOrigin.lerp(mousePos, dt);
        } else if (dragDebounce <= 0.0f) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.scrollY != 0.0f) {
            camera.addZoom(-(float) Math.pow(Math.abs(MouseListener.scrollY * scrollSensitivity),
                    1 / camera.zoom) * -Math.signum(MouseListener.scrollY));
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_COMMA)) {
            reset = true;
        }

        if (reset) {
            camera.position.lerp(new Vector2f(), lerpTime);
            camera.zoom = this.camera.zoom + ((1.0f - camera.zoom) * lerpTime);
            this.lerpTime += 0.01f * dt;

            if (Math.abs(camera.position.x) <= 0.01f &&
                Math.abs(camera.position.y) <= 0.01f &&
                Math.abs(camera.zoom - 1) <= 0.01f) {
                lerpTime = 0.0f;
                camera.position.set(0f, 0f);
                camera.zoom = 1;
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
