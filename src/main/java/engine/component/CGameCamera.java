package engine.component;

import engine.render.Camera;

//TODO: добавить зависимость положения камеры от курсора
public class CGameCamera extends Component {

    private final Camera camera;

    public CGameCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void update(float dt) {
        camera.position.lerp(gameObject.transform.position, dt * 8);
    }
}
