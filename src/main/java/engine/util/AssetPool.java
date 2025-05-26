package engine.util;


import engine.object.SpriteSheet;
import engine.render.Shader;
import engine.render.Texture;
import registry.ShaderType;
import registry.TextureType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static final String SHADER_PATH_TEMPLATE = "asset/shader/%s.glsl";

    private static final String TEXTURE_PATH_TEMPLATE = "asset/texture/%s.png";

    private static final EnumMap<ShaderType, Shader> shaders = new EnumMap<>(ShaderType.class);

    private static final EnumMap<TextureType, Texture> textures = new EnumMap<>(TextureType.class);

    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    private AssetPool() {
    }

    public static Shader getShader(ShaderType shaderType) {
        if (shaders.containsKey(shaderType)) {
            return shaders.get(shaderType);
        } else {
            Shader shader = new Shader(String.format(SHADER_PATH_TEMPLATE, shaderType.name));
            shaders.put(shaderType, shader);

            return shader;
        }
    }

    public static Texture getTexture(TextureType textureType) {
        if (AssetPool.textures.containsKey(textureType)) {
            return AssetPool.textures.get(textureType);
        } else {
            Texture texture = new Texture().init(String.format(TEXTURE_PATH_TEMPLATE, textureType.path));
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
}