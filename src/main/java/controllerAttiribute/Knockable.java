package controllerAttiribute;

import org.joml.Vector2f;

public interface Knockable {

    float getKnockbackResistance();

    void onKnockbackApplied(Vector2f incomingForce);
}
