package controllerAttiribute;

import org.joml.Vector2f;

// TODO: лучше всё-таки вернуть
public interface Knockable {
    float FLYING_TIME = 0.5f;
    float SPEED_REDUCTION_MULTIPLIER = 0.04f;

    float getKnockbackResistance();

    void onKnockbackApplied(Vector2f incomingForce);
}
