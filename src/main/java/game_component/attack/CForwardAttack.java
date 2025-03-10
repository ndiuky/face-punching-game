package game_component.attack;

import component.entity.Bot;
import engine.object.GameObject;
import engine.object.Prefab;
import engine.render.Window;
import org.joml.Vector2f;

import static game_component.attack.AttackGameComponent.AttackState.INACTIVE;
import static game_component.attack.AttackGameComponent.AttackState.MOVING_TO_TARGET;
import static game_component.attack.AttackGameComponent.AttackState.RETRACTING;

public class CForwardAttack extends AttackGameComponent {

    private GameObject fistGO;

    private float fistSpeed = 500;

    private float retractingSpeedMultiplier = 0.7f;

    private GameObject target;

    @Override
    public void executeAttack(final int button, final GameObject target) {
        System.out.println("button = " + button);
        if (target == null || target.getComponent(Bot.class) == null) {
            System.out.println("target cannot be punched in the face");
            return;
        }

        System.out.println("target = " + target.getName());

        if (!INACTIVE.equals(attackState)){
            System.out.println("already performing a punch");
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

        if (fistGO != null){
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
            attackState = RETRACTING;

            System.out.println("пиздюли успешно получены");
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
