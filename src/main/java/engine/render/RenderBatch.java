package engine.render;

import engine.component.Component;
import engine.component.SpriteRenderer;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public abstract class RenderBatch<T extends Component> implements Comparable<RenderBatch<T>> {

    protected int zIndex;

    protected int numElements;

    protected boolean hasRoom;

    protected float[] vertices;

    protected int vaoID, vboID;

    protected final int maxBatchSize;

    public RenderBatch(int maxBatchSize, int zIndex, int vertexSize) {
        this.zIndex = zIndex;
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * 4 * vertexSize];

        numElements = 0;
        hasRoom = true;
    }

    public void start() {
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        int eboID = glGenBuffers();
        int[] indices = generateIndices();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        enableVertexAttribArrays();
    }
    public abstract void render();

    protected abstract int[] generateIndices();

    protected abstract void enableVertexAttribArrays();

    public abstract void addElement(T rendererComponent);

    public boolean hasRoom() {
        return hasRoom;
    }


    public int getZIndex() {
        return zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(zIndex, o.getZIndex());
    }
}