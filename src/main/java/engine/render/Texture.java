package engine.render;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11C.GL_RGBA8;
import static org.lwjgl.opengl.GL13C.GL_CLAMP_TO_BORDER;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Texture {

    private String filepath;

    private transient int texID = -1;

    private int width = -1;

    private int height = -1;

    public Texture() {
    }

    public Texture(int width, int height) {
        this.filepath = "Generated";

        texID = glGenTextures();
        bind();

        setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        setParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);
    }

    public Texture init(String filepath) {
        this.filepath = filepath;

        texID = glGenTextures();
        bind();

        setParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
        setParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);
        setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer widthIntBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightIntBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer channelsIntBuffer = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filepath, widthIntBuffer, heightIntBuffer, channelsIntBuffer, 0);

        if (image != null) {
            width = widthIntBuffer.get(0);
            height = heightIntBuffer.get(0);

            if (channelsIntBuffer.get(0) == 3) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, widthIntBuffer.get(0), heightIntBuffer.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channelsIntBuffer.get(0) == 4) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, widthIntBuffer.get(0), heightIntBuffer.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                throw new RuntimeException("Error: (Texture) Unknown number of channels '" + channelsIntBuffer.get(0) + "'");
            }
        } else {
            throw new RuntimeException("Error: (Texture) Could not load image '" + filepath + "'");
        }

        stbi_image_free(image);

        return this;
    }

    /**
     * Creates a texture with specified width, height and data.
     *
     * @param width  Width of the texture
     * @param height Height of the texture
     * @param data   Picture Data in RGBA format
     * @return Texture from the specified data
     */
    public static Texture createTexture(int width, int height, ByteBuffer data) {
        Texture texture = new Texture();
        texture.setWidth(width);
        texture.setHeight(height);
        texture.texID = glGenTextures();

        texture.bind();

        texture.setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        texture.setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        texture.setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        texture.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        texture.uploadData(GL_RGBA8, width, height, GL_RGBA, data);

        return texture;
    }

    /**
     * Uploads image data with specified width and height.
     *
     * @param width  Width of the image
     * @param height Height of the image
     * @param data   Pixel data of the image
     */
    public void uploadData(int width, int height, ByteBuffer data) {
        uploadData(GL_RGBA8, width, height, GL_RGBA, data);
    }

    /**
     * Uploads image data with specified internal format, width, height and
     * image format.
     *
     * @param internalFormat Internal format of the image data
     * @param width          Width of the image
     * @param height         Height of the image
     * @param format         Format of the image data
     * @param data           Pixel data of the image
     */
    public void uploadData(int internalFormat, int width, int height, int format, ByteBuffer data) {
        glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_UNSIGNED_BYTE, data);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setParameter(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTexID() {
        return texID;
    }

    public String getFilepath() {
        return filepath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        return texID == texture.texID && width == texture.width && height == texture.height && Objects.equals(filepath, texture.filepath);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filepath, texID, width, height);
    }
}