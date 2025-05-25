package util;

import org.joml.Vector2f;
import org.joml.Vector4f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static engine.util.EnginePreferences.CONFIG_FILE_COMMENT_CHAR;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Utils {

    public static ArrayDeque<Vector2f> calculateBezierPath(Vector2f origin, Vector2f destination, boolean isLeftSided, int arcSegments, float approachAngle, float middlePointDistance) {

        float fromToXDivided = (destination.x - origin.x) * approachAngle;
        float fromToYDivided = (destination.y - origin.y) * approachAngle;

        float sideAdjustment = (isLeftSided == (origin.y > destination.y) ? 1 : -1) * middlePointDistance;

        float BX = origin.x + fromToXDivided + fromToYDivided * sideAdjustment;
        float BY = origin.y + fromToYDivided - fromToXDivided * sideAdjustment;

        float iterationStep = 1f / arcSegments;
        float currTime, currTimeSquared, oneMinusCurrTime, oneMinusCurrTimeSquared;

        //TODO: мб заменить ArrayDeque на float[]
        ArrayDeque<Vector2f> path = new ArrayDeque<>();
        for (int i = 0; i <= arcSegments; i++) {
            currTime = iterationStep * i;
            oneMinusCurrTime = 1 - currTime;
            oneMinusCurrTimeSquared = oneMinusCurrTime * oneMinusCurrTime;
            currTimeSquared = currTime * currTime;

            path.add(new Vector2f(
                    oneMinusCurrTimeSquared * origin.x + 2 * oneMinusCurrTime * currTime * BX + currTimeSquared * destination.x,
                    oneMinusCurrTimeSquared * origin.y + 2 * oneMinusCurrTime * currTime * BY + currTimeSquared * destination.y
            ));
        }

        return path;
    }

    // TODO: загрузка конфигурации занимает ~10 мс. Вроде немного, но при чтении сейв-файлов, или чего-то похожего с бОльшим размером файла время может сильно вырасти. Стоит распарралелить
    public static Map<String, List<String>> loadPenisFile(String path) throws IOException {
        if (!path.endsWith(".penis")) {
            throw new IOException("Passed file path doesn't have a '.penis' postfix");
        }

        String source = new String(Files.readAllBytes(Paths.get(path)));
        List<String> properties = Arrays.stream(source
                        .replaceAll("\\" + CONFIG_FILE_COMMENT_CHAR + ".*\\" + CONFIG_FILE_COMMENT_CHAR, "")
                        .split(";"))
                .map(s -> s.trim().replaceAll("\s", ""))
                .filter(s -> !s.isBlank())
                .toList();
        Map<String, List<String>> resultingMap = new HashMap<>(properties.size());

        properties.forEach(property -> {
            int separatorIndex = property.indexOf('=');

            // TODO: выглядит как хуйня, переделать
            if (resultingMap.get(property.substring(0, separatorIndex)) == null) {
                resultingMap.put(
                        property.substring(0, separatorIndex),
                        List.of(property.substring(separatorIndex + 1))
                );
            } else {
                resultingMap.compute(
                        property.substring(0, separatorIndex),
                        (k, v) -> {
                            v.add(property.substring(separatorIndex + 1));

                            return v;
                        }
                );
            }
        });

        return resultingMap;
    }

    public static void rotateVertex(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        vec.x = (x * cos) - (y * sin) + origin.x;
        vec.y = (x * sin) + (y * cos) + origin.y;
    }

    /**
     * Эквивалент оригинального метода, но еще и вычисляет дистанцию, а не принимает её извне.
     * Нужен в случае, если дистанция ранее не вычислялась
     */
    public static Vector2f calculateClampedPosition(Vector2f from, Vector2f to, float maxLength) {
        float distance = from.distance(to);

        return calculateClampedPosition(from, to, maxLength, distance);
    }

    public static Vector2f calculateClampedPosition(Vector2f from, Vector2f to, float maxLength, float distance) {
        if (distance <= maxLength) {
            return new Vector2f(to);
        }

        float t = maxLength / distance;

        float newX = from.x + t * (to.x - from.x);
        float newY = from.y + t * (to.y - from.y);

        return new Vector2f(newX, newY);
    }

    //TODO: добавь точно такой же метод, но с from=0 по умолчанию, ибо чаще всего такой он и приходит
    public static float inverseLerpUnclamped(float from, float to, float midPoint) {
        return from + midPoint / (to - from);
    }

    // TODO: эта штука много где дёргается, стоит кешировать каждый кадр. Возможно также стоит кешировать синус времени, ибо его тоже часто дёргают
    public static float time() {
        return (float) glfwGetTime();
    }

    /**
     * Принимает строку с '%' в качестве аргументов, и любые аргументы.
     * Выводит в консоль
     */
    public static void printf(String string, Object... args) {
        final Iterator<Object> iterator = Arrays.stream(args).toList().iterator();
        System.out.println(
                Arrays.stream(string.split("%"))
                        .map(part -> part.concat(iterator.next().toString()))
                        .collect(Collectors.joining())
        );
    }

    // TODO: убедиться что работает
    public static Vector4f centerAndSizeToCorners(Vector4f centerAndSize) {
        return new Vector4f(
                centerAndSize.x - centerAndSize.z / 2,
                centerAndSize.y - centerAndSize.y / 2,
                centerAndSize.x + centerAndSize.z / 2,
                centerAndSize.y + centerAndSize.y / 2
        );
    }

    // TODO: убедиться что работает
    public static Vector4f cornersToCenterAndSize(Vector4f corners) {
        return new Vector4f(
                (corners.x + corners.z) / 2,
                (corners.y + corners.w) / 2,
                Math.abs(corners.z - corners.x),
                Math.abs(corners.w - corners.y)
        );
    }
}
