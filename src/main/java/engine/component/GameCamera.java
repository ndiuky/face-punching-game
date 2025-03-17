package engine.component;

import engine.render.Camera;
import engine.util.listener.KeyListener;
import engine.util.listener.MouseListener;
import org.joml.Vector2f;

import java.util.Vector;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class GameCamera extends Component{

    private float dragDebounce = 0.032f;
    private final Camera camera;
    private Vector2f clickOrigin;
    private boolean reset = false;
    private float lerpTime = 0.0f;
    private final float dragSensitivity = 30.0f;
    private final float scrollSensitivity = 0.1f;

    public GameCamera(Camera camera) {
        this.camera = camera;
        this.clickOrigin = new Vector2f();
    }

    @Override
    public void update(final float dt) {

        //TODO: переделать с mouseButtonDown на ивенты с флагом выключения
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {

            if (dragDebounce > 0) {
                clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
                dragDebounce -= dt;
                return;
            }
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            camera.position.sub(new Vector2f(mousePos).sub(clickOrigin).mul(dt).mul(dragSensitivity));
            clickOrigin.lerp(mousePos, dt);
        } else if (dragDebounce <= 0.0f) {
            dragDebounce = 0.1f;
        }

        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity),
                    1 / camera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            camera.addZoom(addValue);
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_COMMA)) {
            reset = true;
        }

        if (reset) {
            camera.position.lerp(new Vector2f(), lerpTime);
            camera.setZoom(this.camera.getZoom() +
                    ((1.0f - camera.getZoom()) * lerpTime));
            this.lerpTime += 0.1f * dt;
            if (Math.abs(camera.position.x) <= 1.0f &&
                    Math.abs(camera.position.y) <= 1.0f &&
                    Math.abs(camera.getZoom()-1) <= 0.01f) {
                lerpTime = 0.0f;
                camera.position.set(0f, 0f);
                camera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
