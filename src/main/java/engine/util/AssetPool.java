package engine.util;


import engine.object.SpriteSheet;
import engine.render.Shader;
import engine.render.Texture;

import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static final String SHADER_PATH_TEMPLATE = "asset/shader/%s.glsl";

    private static final Map<String, Shader> shaders = new HashMap<>();

    private static final Map<String, Texture> textures = new HashMap<>();

    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShaderByName(String shaderName) {
        if (shaders.containsKey(shaderName)) {
            return shaders.get(shaderName);
        } else {
            Shader shader = new Shader(getFullShaderPath(shaderName));
            shader.compile();
            shaders.put(shaderName, shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        if (textures.containsKey(resourceName)) {
            return textures.get(resourceName);
        } else {
            Texture texture = new Texture()
                    .init(resourceName);
            textures.put(resourceName, texture);
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
}