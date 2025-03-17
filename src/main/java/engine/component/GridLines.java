package engine.component;

import engine.render.Camera;
import engine.render.DebugDraw;
import engine.util.Color;
import engine.util.EnginePreferences;
import org.joml.Vector2f;

public class GridLines extends Component {

    private final Camera camera;
    //TODO: 14.07.2024 добавить уменьшение/увеличение количества линий при сильном изменении зума
    private final int CLAMP_RATIO = 2;

    public GridLines(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void update(float dt) {
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        int firstX = ((int) (cameraPos.x / EnginePreferences.GRID_SIZE) - 1) * EnginePreferences.GRID_SIZE;
        int firstY = ((int) (cameraPos.y / EnginePreferences.GRID_SIZE) - 1) * EnginePreferences.GRID_SIZE;

        int horizontalLinesAmount = (int) (projectionSize.y * camera.getZoom() / EnginePreferences.GRID_SIZE) + 2;
        int verticalLinesAmount = (int) (projectionSize.x * camera.getZoom() / EnginePreferences.GRID_SIZE) + 2;

        int width = (int) (projectionSize.x * (camera.getZoom())) + EnginePreferences.GRID_SIZE * 2;
        int height = (int) (projectionSize.y * (camera.getZoom())) + EnginePreferences.GRID_SIZE * 2;

        int maxLines = Math.max(verticalLinesAmount, horizontalLinesAmount);
        for (int i = 0; i < maxLines; i++) {
            int x = firstX + (EnginePreferences.GRID_SIZE * i);
            int y = firstY + (EnginePreferences.GRID_SIZE * i);

            if (i < horizontalLinesAmount) {
                DebugDraw.drawLine(new Vector2f(firstX, y), new Vector2f(firstX + width, y), 9, Color.GREY3);
            }

            if (i < verticalLinesAmount) {
                DebugDraw.drawLine(new Vector2f(x, firstY), new Vector2f(x, firstY + height), 9, Color.GREY3);
            }

        }
    }
}