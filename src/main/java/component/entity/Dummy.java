package component.entity;

import controllerAttiribute.Knockable;
import org.joml.Vector2f;

public class Dummy extends Bot implements Knockable {

    private float knockbackResistance = 0;

    private boolean isKnocked = false;

    @Override
    public void start() {
    }

    @Override
    public void update(final float dt) {
        if (isKnocked) {
            flyFromKnock(dt);
        }
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    @Override
    public void onKnockbackApplied(Vector2f incomingForce) {
        knockDir = new Vector2f(incomingForce).mul(1 - knockbackResistance);
        initialPos = new Vector2f(gameObject.transform.position);
        isKnocked = true;
    }


    private final float flyingTime = 0.5f;
    private float currFlyingTime = 0;
    private Vector2f knockDir;
    private Vector2f initialPos;

    private void flyFromKnock(float dt) {

        final float factor = currFlyingTime >= flyingTime ? 1 : (float) (1 - Math.pow(2, -50 * currFlyingTime));

        gameObject.transform.position = new Vector2f(initialPos).add(new Vector2f(knockDir).mul(factor));

        currFlyingTime += dt;

        if (factor == 1) {
            isKnocked = false;
            currFlyingTime = 0;
        }
    }

}
