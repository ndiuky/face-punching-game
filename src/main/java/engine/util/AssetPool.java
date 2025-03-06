package engine.util;


import engine.object.SpriteSheet;
import engine.render.Shader;
import engine.render.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static Map<String, Shader> shaders = new HashMap<>();

    private static Map<String, Texture> textures = new HashMap<>();

    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (shaders.containsKey(file.getAbsolutePath())) {
            return shaders.get(file.getAbsolutePath());
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }
    }

    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (textures.containsKey(file.getAbsolutePath())) {
            return textures.get(file.getAbsolutePath());
        } else {
            Texture texture = new Texture()
                    .init(resourceName);
            textures.put(file.getAbsolutePath(), texture);
            return texture;
        }
    }

    public static void addSpritesheet(String resourceName, SpriteSheet spritesheet) {
        File file = new File(resourceName);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            AssetPool.spriteSheets.put(file.getAbsolutePath(), spritesheet);
        }
    }

    public static SpriteSheet getSpritesheet(String resourceName) {
        File file = new File(resourceName);
        if (!spriteSheets.containsKey(file.getAbsolutePath())){
            throw new RuntimeException("Error: Tried to access spritesheet '" + resourceName + "' and it has not been added to asset pool.");
        }

        return spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
}