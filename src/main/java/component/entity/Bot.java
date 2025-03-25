package component.entity;

import controllerAttiribute.Knockable;
import engine.component.Component;
import org.joml.Vector2f;

public abstract class Bot extends Component implements Knockable {

    //TODO: добавить коллайдер
    protected float knockbackResistance = 0.8f;

    protected boolean isKnocked = false;

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    @Override
    public void onKnockbackApplied(Vector2f incomingForce) {
        knockDir = new Vector2f(incomingForce).mul(1 - knockbackResistance);
        initialPos = new Vector2f(gameObject.transform.position);
        isKnocked = true;
        currFlyingTime = 0;
    }

    private float currFlyingTime;
    private Vector2f knockDir;
    private Vector2f initialPos;

    protected void flyFromKnock(float dt) {

        float factor = currFlyingTime >= FLYING_TIME ? 1 : (float) (1 - Math.pow(2, -50 * currFlyingTime));

        gameObject.transform.position = new Vector2f(initialPos).add(new Vector2f(knockDir).mul(factor));

        currFlyingTime += dt;

        if (factor == 1) {
            isKnocked = false;
            currFlyingTime = 0;
        }
    }
}
