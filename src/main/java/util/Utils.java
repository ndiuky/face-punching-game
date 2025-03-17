package util;

import org.joml.Vector2f;

import java.util.ArrayDeque;

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
}
