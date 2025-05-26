package util;

import static java.lang.Math.pow;

public class AnimationUtils {

    public static final float MAX_SCALE_INTERACTION_DENIED_ANIMATION_DELTA = 0.2f;
    public static final float MAX_Y_ATTACK_ANIMATION_DELTA = 0.6f;
    public static final float MAX_DEGREES_ATTACK_ANIMATION_DELTA = 10f;

    private static final float EASE_OUT_ELASTIC_PARAM = (float) ((2 * Math.PI) / 3);
    private static final float EASE_OUT_BACK_PARAM = 1.70158f;

    public static float easeOutElastic(float t) {
        return (float) (pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * EASE_OUT_ELASTIC_PARAM) + 1);
    }

    public static float easeOutExp(float t) {
        return (float) pow(2, -10 * t);
    }

    public static float easeOutBack(float t) {
        return (float) (1 + (EASE_OUT_BACK_PARAM + 1) * pow(t - 1, 3) + EASE_OUT_BACK_PARAM * pow(t - 1, 2));
    }
}
