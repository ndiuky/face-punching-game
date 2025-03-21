package engine.render;

import engine.util.EnginePreferences;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

    private final Matrix4f projectionMatrix;

    //TODO: убрать зависимость от GRID_SIZE
    private final Vector2f projectionSize = new Vector2f(EnginePreferences.GRID_SIZE * 40.0f, EnginePreferences.GRID_SIZE * 21.0f);

    private final Matrix4f viewMatrix;

    private final Matrix4f inverseProjectionMatrix;

    private final Matrix4f inverseViewMatrix;

    public Vector2f position;

    private float zoom = 1;

    public Camera(Vector2f position) {
        this.position = position;
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        inverseProjectionMatrix = new Matrix4f();
        inverseViewMatrix = new Matrix4f();

        adjustProjection();
    }

    public void adjustProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, projectionSize.x * zoom, 0.0f, projectionSize.y * zoom, 0.0f, 100.0f);
        projectionMatrix.invert(inverseProjectionMatrix);
    }

    public void addZoom(float amount) {
        zoom += amount;
    }

    public Matrix4f getProjectionMatrix() {
        return  projectionMatrix;
    }

    public Vector2f getProjectionSize() {
        return projectionSize;
    }

    //TODO: мб кешировать матрицу для каждого кадра
    public Matrix4f getViewMatrix() {
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f),
                cameraFront.add(position.x, position.y, 0.0f),
                cameraUp);

        viewMatrix.invert(inverseViewMatrix);

        return viewMatrix;
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

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
}