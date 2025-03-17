package util;

import org.joml.Vector2f;

import java.util.ArrayDeque;

public class Utils {


    public static ArrayDeque<Vector2f> calculatePath(Vector2f origin, Vector2f destination, boolean isLeftSided, int arcSegments, float approachAngle, float middlePointDistance) {

        final float botX = origin.x;
        final float botY = origin.y;

        final float destinationX = destination.x;
        final float destinationY = destination.y;

        //TODO: убрать метод calculateBPoint, и перенести вычисление BX и BY по отдельности в этот метод
        final Vector2f BPoint = calculateBPoint(botX, botY, destinationX, destinationY, isLeftSided, approachAngle, middlePointDistance);

        float BX = BPoint.x;
        float BY = BPoint.y;

        final float iterationStep = 1f / arcSegments;
        float currTime, currTimeSquared, oneMinusCurrTime, oneMinusCurrTimeSquared;

        //TODO: мб заменить ArrayDeque на обычный массив
        final ArrayDeque<Vector2f> path = new ArrayDeque<>();
        for (int i = 0; i <= arcSegments; i++) {
            currTime = iterationStep * i;
            oneMinusCurrTime = 1 - currTime;
            oneMinusCurrTimeSquared = oneMinusCurrTime * oneMinusCurrTime;
            currTimeSquared = currTime * currTime;

            path.add(new Vector2f(
                    oneMinusCurrTimeSquared * botX + 2 * oneMinusCurrTime * currTime * BX + currTimeSquared * destinationX,
                    oneMinusCurrTimeSquared * botY + 2 * oneMinusCurrTime * currTime * BY + currTimeSquared * destinationY
            ));
        }

        return path;
    }

    private static Vector2f calculateBPoint(float botX, float botY, float destinationX, float destinationY, boolean isLeftSided, float approachAngle, float middlePointDistance) {
        float fromToXDivided = (destinationX - botX) * approachAngle;
        float fromToYDivided = (destinationY - botY) * approachAngle;

        final float sideAdjustment = (isLeftSided == (botY > destinationY) ? 1 : -1) * middlePointDistance;

        return new Vector2f(
                botX + fromToXDivided + fromToYDivided * sideAdjustment,
                botY + fromToYDivided - fromToXDivided * sideAdjustment
        );
    }
}
