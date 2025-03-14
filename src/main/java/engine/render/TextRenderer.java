package engine.render;

import engine.text.Font;
import engine.util.AssetPool;
import engine.util.Color;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.FontFormatException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.ARBVertexArrayObject.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.glBufferData;
import static org.lwjgl.opengl.GL15C.glBufferSubData;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindFragDataLocation;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

/**
 * This class is performing the rendering process.
 *
 * @author Heiko Brumme
 */
public class TextRenderer {

    private int vao = -1;
    private int vbo = -1;
    private Shader shader;

    private FloatBuffer vertices;
    private int numVertices;
    private boolean drawing;

    private Font font;
    private Font debugFont;

    /**
     * Initializes the renderer.
     */
    public void init() {
        shader = AssetPool.getShader("asset/shader/fontShader.glsl");

        /* Enable blending */
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        /* Create fonts */
        try {
            font = new Font(new FileInputStream("asset/font/Roboto-Bold.ttf"), 128);
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(Renderer.class.getName()).log(Level.CONFIG, null, ex);
            font = new Font();
        }
        debugFont = new Font(12, false);
    }

    /**
     * Clears the drawing area.
     */
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Begin rendering.
     */
    public void begin() {
        if (drawing) {
            throw new IllegalStateException("Renderer is already drawing!");
        }
        /* Setup shader programs */
        setupShaderProgram();
        drawing = true;
        numVertices = 0;
    }

    /**
     * End rendering.
     */
    public void end() {
        if (!drawing) {
            throw new IllegalStateException("Renderer isn't drawing!");
        }
        drawing = false;
        flush();
    }


    public void flush() {
        if (numVertices > 0) {
            vertices.flip();

            if (vao != -1) {
                vao = glGenVertexArrays();
            } else {
                glBindBuffer(vbo, GL_ARRAY_BUFFER);
                specifyVertexAttributes();
            }
            shader.use();

            glBindBuffer(vbo, GL_ARRAY_BUFFER);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

            glDrawArrays(GL_TRIANGLES, 0, numVertices);

            vertices.clear();
            numVertices = 0;
        }
    }

    public int getTextWidth(CharSequence text) {
        return font.getWidth(text);
    }

    public int getTextHeight(CharSequence text) {
        return font.getHeight(text);
    }

    /**
     * Calculates total width of a debug text.
     *
     * @param text The text
     * @return Total width of the text
     */
    public int getDebugTextWidth(CharSequence text) {
        return debugFont.getWidth(text);
    }

    /**
     * Calculates total height of a debug text.
     *
     * @param text The text
     * @return Total width of the text
     */
    public int getDebugTextHeight(CharSequence text) {
        return debugFont.getHeight(text);
    }

    /**
     * Draw text at the specified position.
     *
     * @param text Text to draw
     * @param x    X coordinate of the text position
     * @param y    Y coordinate of the text position
     */
    public void drawText(CharSequence text, float x, float y) {
        font.drawText(this, text, x, y);
    }

    /**
     * Draw debug text at the specified position.
     *
     * @param text Text to draw
     * @param x    X coordinate of the text position
     * @param y    Y coordinate of the text position
     */
    public void drawDebugText(CharSequence text, float x, float y) {
        debugFont.drawText(this, text, x, y);
    }

    /**
     * Draw text at the specified position and color.
     *
     * @param text Text to draw
     * @param x    X coordinate of the text position
     * @param y    Y coordinate of the text position
     * @param c    Color to use
     */
    public void drawText(CharSequence text, float x, float y, Color c) {
        font.drawText(this, text, x, y, c);
    }

    /**
     * Draw debug text at the specified position and color.
     *
     * @param text Text to draw
     * @param x    X coordinate of the text position
     * @param y    Y coordinate of the text position
     * @param c    Color to use
     */
    public void drawDebugText(CharSequence text, float x, float y, Color c) {
        debugFont.drawText(this, text, x, y, c);
    }

    /**
     * Draws the currently bound texture on specified coordinates.
     *
     * @param texture Used for getting width and height of the texture
     * @param x       X position of the texture
     * @param y       Y position of the texture
     */
    public void drawTexture(Texture texture, float x, float y) {
        drawTexture(texture, x, y, Color.WHITE);
    }

    /**
     * Draws the currently bound texture on specified coordinates and with
     * specified color.
     *
     * @param texture Used for getting width and height of the texture
     * @param x       X position of the texture
     * @param y       Y position of the texture
     * @param c       The color to use
     */
    public void drawTexture(Texture texture, float x, float y, Color c) {
        /* Vertex positions */
        float x1 = x;
        float y1 = y;
        float x2 = x1 + texture.getWidth();
        float y2 = y1 + texture.getHeight();

        /* Texture coordinates */
        float s1 = 0f;
        float t1 = 0f;
        float s2 = 1f;
        float t2 = 1f;

        drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param texture   Used for getting width and height of the texture
     * @param x         X position of the texture
     * @param y         Y position of the texture
     * @param regX      X position of the texture region
     * @param regY      Y position of the texture region
     * @param regWidth  Width of the texture region
     * @param regHeight Height of the texture region
     */
    public void drawTextureRegion(Texture texture, float x, float y, float regX, float regY, float regWidth, float regHeight) {
        drawTextureRegion(texture, x, y, regX, regY, regWidth, regHeight, Color.WHITE);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param texture   Used for getting width and height of the texture
     * @param x         X position of the texture
     * @param y         Y position of the texture
     * @param regX      X position of the texture region
     * @param regY      Y position of the texture region
     * @param regWidth  Width of the texture region
     * @param regHeight Height of the texture region
     * @param c         The color to use
     */
    public void drawTextureRegion(Texture texture, float x, float y, float regX, float regY, float regWidth, float regHeight, Color c) {
        /* Vertex positions */
        float x1 = x;
        float y1 = y;
        float x2 = x + regWidth;
        float y2 = y + regHeight;

        /* Texture coordinates */
        float s1 = regX / texture.getWidth();
        float t1 = regY / texture.getHeight();
        float s2 = (regX + regWidth) / texture.getWidth();
        float t2 = (regY + regHeight) / texture.getHeight();

        drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, c);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param x1 Bottom left x position
     * @param y1 Bottom left y position
     * @param x2 Top right x position
     * @param y2 Top right y position
     * @param s1 Bottom left s coordinate
     * @param t1 Bottom left t coordinate
     * @param s2 Top right s coordinate
     * @param t2 Top right t coordinate
     */
    public void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2) {
        drawTextureRegion(x1, y1, x2, y2, s1, t1, s2, t2, Color.WHITE);
    }

    /**
     * Draws a texture region with the currently bound texture on specified
     * coordinates.
     *
     * @param x1 Bottom left x position
     * @param y1 Bottom left y position
     * @param x2 Top right x position
     * @param y2 Top right y position
     * @param s1 Bottom left s coordinate
     * @param t1 Bottom left t coordinate
     * @param s2 Top right s coordinate
     * @param t2 Top right t coordinate
     * @param c  The color to use
     */
    public void drawTextureRegion(float x1, float y1, float x2, float y2, float s1, float t1, float s2, float t2, Color c) {
        if (vertices.remaining() < 8 * 6) {
            /* We need more space in the buffer, so flush it */
            flush();
        }

        float r = c.r;
        float g = c.g;
        float b = c.b;
        float a = c.a;

        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
        vertices.put(x1).put(y2).put(r).put(g).put(b).put(a).put(s1).put(t2);
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);

        vertices.put(x1).put(y1).put(r).put(g).put(b).put(a).put(s1).put(t1);
        vertices.put(x2).put(y2).put(r).put(g).put(b).put(a).put(s2).put(t2);
        vertices.put(x2).put(y1).put(r).put(g).put(b).put(a).put(s2).put(t1);

        numVertices += 6;
    }

    /**
     * Dispose renderer and clean up its used data.
     */
    public void dispose() {
        MemoryUtil.memFree(vertices);

        if (vao != -1) {
            glDeleteVertexArrays(vao);
        }
        glDeleteBuffers(vbo);
        shader.detach();

        font.dispose();
        debugFont.dispose();
    }

    /**
     * Setups the default shader shader.
     */
    public void setupShaderProgram() {
        /* Generate Vertex Array Object */
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        /* Generate Vertex Buffer Object */
        vbo = glGenBuffers();
        glBindBuffer(vbo, GL_ARRAY_BUFFER);

        /* Create FloatBuffer */
        vertices = MemoryUtil.memAllocFloat(4096);

        /* Upload null data to allocate storage for the VBO */
        long size = vertices.capacity() * Float.BYTES;
        glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);

        /* Initialize variables */
        numVertices = 0;
        drawing = false;

        glBindFragDataLocation(shader.getShaderProgramID(), 0, "fragColor");

        /* Delete linked shaders */
//        vertexShader.delete();
//        fragmentShader.delete();

        /* Get width and height of framebuffer */
        long window = GLFW.glfwGetCurrentContext();
        int width, height;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(window, widthBuffer, heightBuffer);
            width = widthBuffer.get();
            height = heightBuffer.get();
        }

        /* Specify Vertex Pointers */
        specifyVertexAttributes();

        /* Set texture uniform */
        int uniTex = shader.getUniformLocation("texImage");
        shader.setUniform(uniTex, 0);

        /* Set model matrix to identity matrix */
        Matrix4f model = new Matrix4f();
        int uniModel = shader.getUniformLocation("model");
        shader.setUniform(uniModel, model);

        /* Set view matrix to identity matrix */
        Matrix4f view = new Matrix4f();
        int uniView = shader.getUniformLocation("view");
        shader.setUniform(uniView, view);

        /* Set projection matrix to an orthographic projection */

        Matrix4f ortho = new Matrix4f();

        float tx = -(width + 0f) / (width - 0f);
        float ty = -(height + 0f) / (height - 0f);
        float tz = -(1 + -1) / (1 - -1);

        ortho.m00(2f / (width - 0f));
        ortho.m11(2f / (height - 0f));
        ortho.m22(-2f / (1 - -1));
        ortho.m03(tx);
        ortho.m13(ty);
        ortho.m23(tz);

        int uniProjection = shader.getUniformLocation("projection");
        shader.setUniform(uniProjection, ortho);
    }

    /**
     * Specifies the vertex pointers.
     */
    private void specifyVertexAttributes() {
        /* Specify Vertex Pointer */
        int posAttrib = shader.getAttributeLocation("position");
        glEnableVertexAttribArray(posAttrib);
        glVertexAttribPointer(posAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = shader.getAttributeLocation("color");
        glEnableVertexAttribArray(colAttrib);
        glVertexAttribPointer(colAttrib, 4, GL_FLOAT, false, 8 * Float.BYTES, 2 * Float.BYTES);

        /* Specify Texture Pointer */
        int texAttrib = shader.getAttributeLocation("texcoord");
        glEnableVertexAttribArray(texAttrib);
        glVertexAttribPointer(texAttrib, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
    }

}
