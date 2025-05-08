package engine.util;


import engine.object.SpriteSheet;
import engine.render.Shader;
import engine.render.Texture;
import registry.TextureType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static final String SHADER_PATH_TEMPLATE = "asset/shader/%s.glsl";

    private static final String TEXTURE_PATH_TEMPLATE = "asset/texture/%s.png";

    private static final Map<String, Shader> shaders = new HashMap<>();

    private static final EnumMap<TextureType, Texture> textures = new EnumMap<>(TextureType.class);

    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    private AssetPool() {
    }

    /**
     * @param shaderName name of the shader in 'asset/shader' directory without postfix
     */
    public static Shader getShader(String shaderName) {
        if (shaders.containsKey(shaderName)) {
            return shaders.get(shaderName);
        } else {
            Shader shader = new Shader(getFullShaderPath(shaderName));
            shader.compile();
            shaders.put(shaderName, shader);

            return shader;
        }
    }

    public static Texture getTexture(TextureType textureType) {
        if (AssetPool.textures.containsKey(textureType)) {
            return AssetPool.textures.get(textureType);
        } else {
            Texture texture = new Texture().init(getFullTexturePath(textureType.path));
            AssetPool.textures.put(textureType, texture);

            return texture;
        }
    }

    public static void addSpriteSheet(String resourceName, SpriteSheet spritesheet) {
        if (!spriteSheets.containsKey(resourceName)) {
            spriteSheets.put(resourceName, spritesheet);
        }
    }

    public static SpriteSheet getSpritesheet(String resourceName) {
        return spriteSheets.getOrDefault(resourceName, null);
    }

    private static String getFullShaderPath(String shaderName) {
        return String.format(SHADER_PATH_TEMPLATE, shaderName);
    }

    private static String getFullTexturePath(String textureName) {
        return String.format(TEXTURE_PATH_TEMPLATE, textureName);
    }
}