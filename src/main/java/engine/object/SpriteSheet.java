package engine.object;

import engine.render.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {

    public Texture texture;

    public final List<Sprite> sprites = new ArrayList<>();

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        int currentX = 0;
        int currentY = texture.height - spriteHeight;

        for (int i = 0; i < numSprites; i++) {
            float topY = (currentY + spriteHeight) / (float) texture.height;
            float rightX = (currentX + spriteWidth) / (float) texture.width;
            float leftX = currentX / (float) texture.width;
            float bottomY = currentY / (float) texture.height;

            sprites.add(
                    new Sprite(
                            texture,
                            new Vector2f[]{
                                    new Vector2f(rightX, topY),
                                    new Vector2f(rightX, bottomY),
                                    new Vector2f(leftX, bottomY),
                                    new Vector2f(leftX, topY)
                            },
                            spriteWidth,
                            spriteHeight));

            currentX += spriteWidth + spacing;
            if (currentX >= texture.width) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }
}