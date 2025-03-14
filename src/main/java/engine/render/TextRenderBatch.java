package engine.render;

import engine.component.CTextRenderer;

public class TextRenderBatch extends RenderBatch<CTextRenderer>{
    public TextRenderBatch(int maxBatchSize, int zIndex, int vertexSize) {
        super(maxBatchSize, zIndex, vertexSize);
    }

    @Override
    public void render() {

    }

    @Override
    protected int[] generateIndices() {
        return new int[0];
    }

    @Override
    protected void enableVertexAttribArrays() {

    }

    @Override
    public void addElement(CTextRenderer rendererComponent) {

    }
}
