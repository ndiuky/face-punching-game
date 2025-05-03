package util;

import org.joml.Vector2f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static engine.util.EnginePreferences.CONFIG_FILE_COMMENT_CHAR;

public class Utils {

    public static ArrayDeque<Vector2f> calculatePath(Vector2f origin, Vector2f destination, boolean isLeftSided, int arcSegments, float approachAngle, float middlePointDistance) {

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

    public static Map<String, String> loadPenisFile(String path) throws IOException {
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
        Map<String, String> resultingMap = new HashMap<>(properties.size());

        properties.forEach(property -> {
            int separatorIndex = property.indexOf('=');

            resultingMap.put(
                    property.substring(0, separatorIndex),
                    property.substring(separatorIndex + 1)
            );
        });

        return resultingMap;
    }
}
