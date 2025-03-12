package engine.util;


import engine.object.SpriteSheet;
import engine.render.Shader;
import engine.render.Texture;

import java.util.HashMap;
import java.util.Map;

public class AssetPool {

    private static final Map<String, Shader> shaders = new HashMap<>();

    private static final Map<String, Texture> textures = new HashMap<>();

    private static final Map<String, SpriteSheet> spriteSheets = new HashMap<>();

    public static Shader getShader(String resourceName) {
        if (shaders.containsKey(resourceName)) {
            return shaders.get(resourceName);
        } else {
            Shader shader = new Shader(resourceName);
            shader.compile();
            shaders.put(resourceName, shader);
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

    public static void addSpritesheet(String resourceName, SpriteSheet spritesheet) {
        if (!spriteSheets.containsKey(resourceName)) {
            spriteSheets.put(resourceName, spritesheet);
        }
    }

    public static SpriteSheet getSpritesheet(String resourceName) {
//        if (!spriteSheets.containsKey(resourceName)){
//            throw new RuntimeException("Error: Tried to access spritesheet '" + resourceName + "' and it has not been added to asset pool.");
//        }

        return spriteSheets.getOrDefault(resourceName, null);
    }
}