package engine.render;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private static long calculatedValuesAtFrame = -1;

    private static Matrix4f cachedViewMatrix;

    public final Matrix4f projectionMatrix;

    public final Matrix4f inverseProjectionMatrix;

    public final Matrix4f inverseViewMatrix;

    public Vector2f projectionSize;

    public Vector2f position;

    public float zoom = 1f;

    private final Matrix4f viewMatrix;

    public Camera(Vector2f position) {
        this.position = position;
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        inverseProjectionMatrix = new Matrix4f();
        inverseViewMatrix = new Matrix4f();
        updateProjectionSize();

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

    public Matrix4f getViewMatrix() {
        if (calculatedValuesAtFrame == Window.currentFrame) {
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
        calculatedValuesAtFrame = Window.currentFrame;

        return cachedViewMatrix;
    }

    public void updateProjectionSize(){
        projectionSize = new Vector2f(Window.getWidth(), Window.getHeight());
    }
}