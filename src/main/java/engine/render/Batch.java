package engine.render;

import static engine.util.EnginePreferences.DEFAULT_QUAD_VERTICES_AMOUNT;
import static engine.util.EnginePreferences.EBO_QUAD_VERTICES_AMOUNT;

//TODO: добавить обязательный Comparable
public abstract class Batch {

    abstract void render();

    protected int[] generateIndices(int maxBatchSize) {
        int[] elements = new int[maxBatchSize * DEFAULT_QUAD_VERTICES_AMOUNT];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        int offsetArrayIndex = DEFAULT_QUAD_VERTICES_AMOUNT * index;
        int offset = EBO_QUAD_VERTICES_AMOUNT * index;

        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset;

        elements[offsetArrayIndex + 3] = offset;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }
}
