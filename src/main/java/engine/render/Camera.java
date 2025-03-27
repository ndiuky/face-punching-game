package engine.render;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

//TODO: размер мир и объектов зависит от разрешения окна игры. Исправляется фиксированным значением projectionSize, но это еще нужно правильно реализовать.
public class Camera {

    private final Matrix4f projectionMatrix;

    private final Vector2f projectionSize;

    private final Matrix4f viewMatrix;

    private final Matrix4f inverseProjectionMatrix;

    private final Matrix4f inverseViewMatrix;

    public Vector2f position;

    private static long calculatedValuesAtFrame = -1;

    private static Matrix4f cachedViewMatrix;

    private float zoom = 1f;

    public Camera(Vector2f position) {
        this.position = position;
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        inverseProjectionMatrix = new Matrix4f();
        inverseViewMatrix = new Matrix4f();
        projectionSize = new Vector2f(Window.getWidth(), Window.getHeight());

        adjustProjection();
    }

    public Camera() {
        this(new Vector2f());
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(-projectionSize.x / zoom, projectionSize.x / zoom, -projectionSize.y / zoom, projectionSize.y / zoom, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjectionMatrix);
    }

    public void addZoom(float amount) {
        if (zoom + amount <= 0) {
            return;
        }

        zoom += amount;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    public Matrix4f getViewMatrix() {
        if (calculatedValuesAtFrame == Window.getCurrentFrame()) {
            return cachedViewMatrix;
        }

        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);

        viewMatrix.invert(inverseViewMatrix);

        cachedViewMatrix = viewMatrix;
        calculatedValuesAtFrame = Window.getCurrentFrame();

        return cachedViewMatrix;
    }

    public Matrix4f getInverseProjectionMatrix() {
        return inverseProjectionMatrix;
    }

    public Matrix4f getInverseViewMatrix() {
        return inverseViewMatrix;
    }

    public float getZoom() {
        return zoom;
    }

    /**
     * Передашь 0 - получишь эксепшен. Take it or leave it
     */
    public Camera setZoom(float zoom) {
        this.zoom = zoom;

        return this;
    }
}