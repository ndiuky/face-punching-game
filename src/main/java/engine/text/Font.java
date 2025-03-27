package engine.text;

import engine.render.Texture;
import engine.util.EnginePreferences;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.awt.Font.PLAIN;
import static java.awt.Font.TRUETYPE_FONT;

public class Font {

    private static final Map<Character, Glyph> glyphs = new HashMap<>();

    private static final Texture texture;

    static {
        try {
            texture = createFontTexture(java.awt.Font.createFont(TRUETYPE_FONT, new FileInputStream("asset/font/Roboto-Bold.ttf")).deriveFont(PLAIN, EnginePreferences.FONT_RESOLUTION));
        } catch (Exception ex) {
            throw new RuntimeException("Unable to create font texture: " + ex.getMessage());
        }
    }

    private static int fontHeight;

    //TODO: переделать текстуру шрифта с длинной полоски на что-то ближе к квадрату
    private static Texture createFontTexture(java.awt.Font font) {

        int imageWidth = 0;
        int imageHeight = 0;

        for (int i = 32; i < 256; i++) {
            if (i == 127) {
                continue;
            }
            char c = (char) i;
            BufferedImage ch = createCharImage(font, c);
            if (ch == null) {
                continue;
            }

            // Этот и все последующие '+1', нужны для того, чтобы не допускать ошибок связанных с неточностью чисел с плавающей точкой.
            // Если располагать каждый гриф вплотную друг к другу, будут появляться артефакты, по сути куски других букв. Мне такого не
            // надо, поэтому пространство занимаемое каждым глифом было увеличено на 1 пиксель. Сам размер глифа при этом никак не меняется.
            imageWidth += ch.getWidth() + 1;
            imageHeight = Math.max(imageHeight, ch.getHeight());
        }

        fontHeight = imageHeight;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        int x = 0;

        for (int i = 32; i < 256; i++) {
            if (i == 127) {
                continue;
            }

            char c = (char) i;
            BufferedImage charImage = createCharImage(font, c);

            if (charImage == null) {
                continue;
            }

            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();

            Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight);
            g.drawImage(charImage, x, 0, null);
            x += ch.width + 1;
            glyphs.put(c, ch);
        }

        AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
        transform.translate(0, -image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = operation.filter(image, null);

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixels[i * width + j];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        Texture fontTexture = Texture.createTexture(width, height, buffer);
        MemoryUtil.memFree(buffer);
        return fontTexture;
    }

    private static BufferedImage createCharImage(java.awt.Font font, char c) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.dispose();

        int charWidth = metrics.charWidth(c);
        int charHeight = metrics.getHeight();

        if (charWidth == 0) {
            return null;
        }

        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setPaint(java.awt.Color.WHITE);
        g.drawString(String.valueOf(c), 0, metrics.getAscent());
        g.dispose();
        return image;
    }

    public static int getWidth(CharSequence text) {
        int width = 0;
        int lineWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                width = Math.max(width, lineWidth);
                lineWidth = 0;
                continue;
            }
            if (c == '\r') {
                continue;
            }
            Glyph g = glyphs.get(c);
            lineWidth += g.width;
        }
        width = Math.max(width, lineWidth);
        return width;
    }

    public static int getHeight(CharSequence text) {
        int height = 0;
        int lineHeight = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                height += lineHeight;
                lineHeight = 0;
                continue;
            }
            if (c == '\r') {
                continue;
            }
            Glyph g = glyphs.get(c);
            lineHeight = Math.max(lineHeight, g.height);
        }
        height += lineHeight;
        return height;
    }

    public static void bind() {
        texture.bind();
    }

    public static void unbind() {
        texture.unbind();
    }

    public static Texture getTexture() {
        return texture;
    }

    public static Vector2f fillInTextMetadata(final List<Glyph> content, final float sizeModifier, final List<Vector2f> positions, String textContent) {
//        int textHeight = getHeight(textContent);

        float drawX = 0;
        float drawY = 0;
//        if (textHeight > fontHeight) {
//            drawY += textHeight - fontHeight;
//        }

        //TODO: переделать алгоритм вычисления общей ширины, он отвратителен
        float fullHeight = fontHeight;
        float lineWidth = 0;
        float fullWidth = 0;

        for (char ch : textContent.toCharArray()) {
//            if (ch == '\n') {
//                drawY -= fontHeight;
//                fullHeight += fontHeight;
//                drawX = 0;
//
//                if (lineWidth > fullWidth){
//                    fullWidth = lineWidth;
//                }
//
//                lineWidth = 0;
//
//                continue;
//            }
//            if (ch == '\r') {
//                continue;
//            }
            Glyph g = glyphs.get(ch);
            content.add(g);
            positions.add(new Vector2f(drawX, drawY));
            drawX += g.width * sizeModifier;
            lineWidth += g.width * sizeModifier;
        }

        if (fullWidth == 0) {
            fullWidth = lineWidth;
        }

        return new Vector2f(fullWidth, fullHeight);
    }
}