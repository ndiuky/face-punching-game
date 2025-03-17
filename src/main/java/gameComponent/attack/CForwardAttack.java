package gameComponent.attack;

import component.entity.Bot;
import controllerAttiribute.Knockable;
import engine.component.Component;
import engine.object.GameObject;
import engine.object.Prefab;
import engine.render.Window;
import org.joml.Vector2f;

import static gameComponent.attack.AttackGameComponent.AttackState.INACTIVE;
import static gameComponent.attack.AttackGameComponent.AttackState.MOVING_TO_TARGET;
import static gameComponent.attack.AttackGameComponent.AttackState.RETRACTING;

public class CForwardAttack extends AttackGameComponent {

    private GameObject fistGO;

    private float fistSpeed = 1000;

    private float retractingSpeedMultiplier = 0.6f;

    private GameObject target;

    @Override
    public void executeAttack(final int button, final GameObject target) {
        if (!INACTIVE.equals(attackState)
                || target == null
                || target.getComponent(Bot.class) == null) {
            return;
        }

        fistGO = Prefab.generateSpriteObject(fistSprite, 15, 15, bot.gameObject.getZIndex() - 1);
        fistGO.transform.position = new Vector2f(bot.gameObject.transform.position);
        Window.getCurrentScene().addGameObjectToScene(fistGO);

        this.target = target;

        attackState = MOVING_TO_TARGET;
    }

    @Override
    public void stopExecution() {
        attackState = INACTIVE;
        target = null;

        if (fistGO != null) {
            fistGO.destroy();
        }
    }

    @Override
    public void update(final float dt) {
        if (MOVING_TO_TARGET.equals(attackState)) {
            moveFistToTarget(dt);
        } else if (RETRACTING.equals(attackState)) {
            retractFist(dt);
        }
    }

    //TODO: не вычислять каждый раз направление движения, а сделать это один раз перед началом движения
    private void moveFistToTarget(float dt) {
        final Vector2f dir = new Vector2f(target.transform.position).sub(new Vector2f(fistGO.transform.position));

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(fistSpeed * dt).absolute().length()) {

            fistGO.transform.position = new Vector2f(target.transform.position);

            for (Component component : target.getAllComponents()){
                if (component instanceof Knockable){
                    ((Knockable) component).onKnockbackApplied(dir.normalize().mul(fistSpeed/25));
                    break;
                }
            }

            attackState = RETRACTING;

            System.out.println("пиздюли успешно доставлены");
        } else {
            fistGO.transform.position.add(new Vector2f(dir).normalize().mul(fistSpeed * dt));
        }
    }

    private void retractFist(final float dt) {
        final Vector2f dir = new Vector2f(bot.gameObject.transform.position).sub(new Vector2f(fistGO.transform.position));

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(fistSpeed * retractingSpeedMultiplier * dt).absolute().length()) {

            target = null;
            fistGO.destroy();
            attackState = INACTIVE;
        } else {
            fistGO.transform.position.add(new Vector2f(dir).normalize().mul(fistSpeed * retractingSpeedMultiplier * dt));
        }
    }
}
