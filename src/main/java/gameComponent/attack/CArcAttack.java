package gameComponent.attack;

import component.entity.Bot;
import controllerAttiribute.Knockable;
import engine.component.Component;
import engine.object.GameObject;
import engine.object.Prefab;
import engine.render.Window;
import org.joml.Math;
import org.joml.Vector2f;

import java.util.ArrayDeque;

import static controllerAttiribute.Knockable.SPEED_REDUCTION_MULTIPLIER;
import static gameComponent.attack.AttackGameComponent.AttackState.INACTIVE;
import static gameComponent.attack.AttackGameComponent.AttackState.MOVING_TO_TARGET;
import static gameComponent.attack.AttackGameComponent.AttackState.RETRACTING;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static util.Utils.calculatePath;

public class CArcAttack extends AttackGameComponent {

    private ArrayDeque<Vector2f> path = new ArrayDeque<>();

    //Angle from 0 to 1
    private float approachAngle = 0.85f;

    private float middlePointDistance = 1.5f;

    private byte arcSegments = 16;

    Vector2f currentDestination;

    private GameObject fistGO;

    private float fistSpeed = 1000;

    private float retractingSpeedMultiplier = 0.6f;

    @Override
    public void executeAttack(int button, GameObject target) {
        if (!INACTIVE.equals(attackState)
                || target == null
                || target.getComponent(Bot.class) == null) {
            return;
        }

        fistGO = Prefab.generateSpriteObject(fistSprite, 15, 15, bot.gameObject.getZIndex() - 1);
        fistGO.transform.position = new Vector2f(bot.gameObject.transform.position);
        Window.getCurrentScene().addGameObjectToScene(fistGO);

        this.target = target;

        if (path.isEmpty()) {

            if (button == GLFW_MOUSE_BUTTON_1) {
                path = calculatePath(bot.gameObject.transform.position, target.transform.position, true, arcSegments, approachAngle, middlePointDistance);
            } else if (button == GLFW_MOUSE_BUTTON_2) {
                path = calculatePath(bot.gameObject.transform.position, target.transform.position, false, arcSegments, approachAngle, middlePointDistance);
            }
        }

        attackState = MOVING_TO_TARGET;
    }

    @Override
    public void stopExecution() {
        attackState = INACTIVE;
        target = null;
        currentDestination = null;
        path.clear();

        if (fistGO != null) {
            fistGO.destroy();
        }
    }

    @Override
    public void update(float dt) {
        if (MOVING_TO_TARGET.equals(attackState)) {
            moveFistToTarget(dt);
        } else if (RETRACTING.equals(attackState)) {
            retractFist(dt);
        }
    }

    private void moveFistToTarget(float dt) {
        if (currentDestination == null) {
            currentDestination = path.pop();
        }

        Vector2f dir = new Vector2f(currentDestination).sub(new Vector2f(fistGO.transform.position));

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(fistSpeed * dt).absolute().length()) {
            if (path.isEmpty()) {
                fistGO.transform.position = currentDestination;

                for (Component component : target.getAllComponents()) {
                    if (component instanceof Knockable) {

                        float radians = Math.toRadians(90 * (1 - approachAngle));

                        float cos = Math.cos(radians);
                        float sin = Math.sin(radians);

                        dir.normalize();
                        ((Knockable) component).onKnockbackApplied(new Vector2f(dir.x * cos + dir.y * -sin, dir.x * sin + dir.y * cos).normalize().mul(fistSpeed * SPEED_REDUCTION_MULTIPLIER));

                        break;
                    }
                }

                attackState = RETRACTING;

                System.out.println("пиздюли успешно доставлены");

                return;
            }

            currentDestination = path.pop();
            moveFistToTarget(dt);
        } else {
            fistGO.transform.position.add(new Vector2f(dir).normalize().mul(fistSpeed * dt));
        }
    }

    private void retractFist(float dt) {
        Vector2f dir = new Vector2f(bot.gameObject.transform.position).sub(new Vector2f(fistGO.transform.position));

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(fistSpeed * retractingSpeedMultiplier * dt).absolute().length()) {

            target = null;
            fistGO.destroy();
            attackState = INACTIVE;
            currentDestination = null;
        } else {
            fistGO.transform.position.add(new Vector2f(dir).normalize().mul(fistSpeed * retractingSpeedMultiplier * dt));
        }
    }
}
