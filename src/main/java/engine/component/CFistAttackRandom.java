package engine.component;

import engine.event.MouseEventListener;
import engine.object.GameObject;
import engine.object.Prefab;
import engine.object.Sprite;
import engine.render.DebugDraw;
import engine.render.Window;
import engine.util.AssetPool;
import engine.util.Color;
import engine.util.EnginePreferences;
import engine.util.Interactable;
import engine.util.listener.MouseListener;
import org.joml.Vector2f;
import registry.TextureType;
import util.InteractionType;

import java.util.ArrayDeque;
import java.util.Random;

import static engine.component.CFistAttackRandom.AttackState.INACTIVE;
import static engine.component.CFistAttackRandom.AttackState.MOVING_TO_TARGET;
import static engine.component.CFistAttackRandom.AttackState.RETRACTING;
import static engine.component.CFistAttackRandom.AttackType.ARC_LEFT;
import static engine.component.CFistAttackRandom.AttackType.ARC_RIGHT;
import static engine.event.MouseEventProducer.listenToMousePress;
import static engine.event.MouseEventProducer.listenToMouseRelease;
import static engine.util.EngineUtils.calculateClampedPosition;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static util.Utils.calculatePath;

public class CFistAttackRandom extends Component implements MouseEventListener {

    private static final Sprite FIST_SPRITE = new Sprite(AssetPool.getTexture(TextureType.FIST));

    private static final float TIME_BETWEEN_ATTACKS = 0.25f;

    private float timeSinceLastAttack = Float.MAX_VALUE;

    private AttackType lastPerformedAttack = AttackType.STRAIGHT;

    private AttackType currentAttack;

    protected AttackState attackState = INACTIVE;

    private boolean isLastAttackPerformed = true;

    private boolean isAttemptingToAttack;

    //======

    private GameObject fistGO;

    private float fistSpeed = 30;

    private float maxAttackDistance = 3;

    private float retractingSpeedMultiplier = 0.7f;

    private Vector2f targetPos;

    GameObject targetGameobject;

    //======

    private ArrayDeque<Vector2f> path = new ArrayDeque<>();

    //Angle from 0 to 1
    private float approachAngle = 0.85f;

    private float middlePointDistance = 1.5f;

    private byte arcSegments = 16;

    Vector2f currentDestination;

    @Override
    public void start() {
        listenToMousePress(GLFW_MOUSE_BUTTON_1, this);
        listenToMouseRelease(GLFW_MOUSE_BUTTON_1, this);
    }

    @Override
    public void update(float dt) {
        DebugDraw.drawCircle(gameObject.transform.position, maxAttackDistance, 0, Color.GREY5, 1, EnginePreferences.DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH);

        if (!attackState.equals(INACTIVE)) {
            executeCurrentAttack(dt);
        } else if (isAttemptingToAttack) {
            if (timeSinceLastAttack >= TIME_BETWEEN_ATTACKS && isLastAttackPerformed) {
                applyNewRandomAttack();
                timeSinceLastAttack = 0;
                isLastAttackPerformed = false;
            }
        }

        timeSinceLastAttack += dt;
    }

    private void executeCurrentAttack(float dt) {
        if (INACTIVE.equals(attackState)) {
            return;
        } else if (RETRACTING.equals(attackState)) {
            retractFist(dt);
            return;
        }

        switch (currentAttack) {
            case STRAIGHT -> {
                straightMoveFistToTarget(dt);
            }
            case ARC_LEFT, ARC_RIGHT -> {
                arcMoveFistToTarget(dt);
            }
        }
    }

    private void applyNewRandomAttack() {
        //TODO: сделать покрасивее, вынести константы
        int i = (lastPerformedAttack.ordinal() + new Random().nextInt(1, AttackType.values().length)) % AttackType.values().length;
        currentAttack = AttackType.values()[i];
        attackState = MOVING_TO_TARGET;

        targetGameobject = MouseListener.getGameObjectUnderCursor();
        boolean isObjectInteractable = targetGameobject != null && targetGameobject.isInteractable();
        float distance = isObjectInteractable
                ? targetGameobject.transform.position.distance(gameObject.transform.position)
                : MouseListener.getOrtho().distance(gameObject.transform.position);

        if (isObjectInteractable && distance < maxAttackDistance) {
            targetPos = new Vector2f(targetGameobject.transform.position);
        } else {
            targetPos = calculateClampedPosition(gameObject.transform.position, MouseListener.getOrtho(), maxAttackDistance, distance);
            targetGameobject = null;
        }

        if (currentAttack == ARC_LEFT) {
            path = calculatePath(gameObject.transform.position, targetPos, true, arcSegments, approachAngle, middlePointDistance);
        } else if (currentAttack == ARC_RIGHT) {
            path = calculatePath(gameObject.transform.position, targetPos, false, arcSegments, approachAngle, middlePointDistance);
        }

        fistGO = Prefab.generateSpriteObject(FIST_SPRITE, 0.4f, gameObject.getZIndex() - 1);
        fistGO.transform.position = new Vector2f(gameObject.transform.position);
        Window.getCurrentScene().addGameObjectToScene(fistGO);

        lastPerformedAttack = currentAttack;
    }

    private void straightMoveFistToTarget(float dt) {
        Vector2f dir = new Vector2f(targetPos).sub(new Vector2f(fistGO.transform.position));

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(fistSpeed * dt).absolute().length()) {

            fistGO.transform.position = new Vector2f(targetPos);

            if (targetGameobject != null) {
                for (Component component : targetGameobject.getAllComponents()) {
//                        if (component instanceof Knockable) {
//
//                            float radians = (float) Math.toRadians(90 * (1 - approachAngle));
//
//                            float cos = (float) Math.cos(radians);
//                            float sin = (float) Math.sin(radians);
//
//                            dir.normalize();
//                            ((Knockable) component).onKnockbackApplied(new Vector2f(dir.x * cos + dir.y * -sin, dir.x * sin + dir.y * cos).normalize().mul(fistSpeed * SPEED_REDUCTION_MULTIPLIER));
//
//                            break;
//                        }
                    if (component instanceof Interactable) {
                        ((Interactable) component).onInteraction(InteractionType.ATTACK);
                        break;
                    }
                }
            }

            attackState = RETRACTING;
        } else {
            fistGO.transform.position.add(new Vector2f(dir).normalize().mul(fistSpeed * dt));
        }
    }

    private void arcMoveFistToTarget(float dt) {
        if (currentDestination == null) {
            currentDestination = path.pop();
        }

        Vector2f dir = new Vector2f(currentDestination).sub(new Vector2f(fistGO.transform.position));

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(fistSpeed * dt).absolute().length()) {
            if (path.isEmpty()) {
                fistGO.transform.position = currentDestination;

                if (targetGameobject != null) {
                    for (Component component : targetGameobject.getAllComponents()) {
//                        if (component instanceof Knockable) {
//
//                            float radians = (float) Math.toRadians(90 * (1 - approachAngle));
//
//                            float cos = (float) Math.cos(radians);
//                            float sin = (float) Math.sin(radians);
//
//                            dir.normalize();
//                            ((Knockable) component).onKnockbackApplied(new Vector2f(dir.x * cos + dir.y * -sin, dir.x * sin + dir.y * cos).normalize().mul(fistSpeed * SPEED_REDUCTION_MULTIPLIER));
//
//                            break;
//                        }
                        if (component instanceof Interactable) {
                            ((Interactable) component).onInteraction(InteractionType.ATTACK);
                            break;
                        }
                    }
                }

                attackState = RETRACTING;

                return;
            }

            currentDestination = path.pop();
            arcMoveFistToTarget(dt);
        } else {
            fistGO.transform.position.add(new Vector2f(dir).normalize().mul(fistSpeed * dt));
        }
    }

    private void retractFist(float dt) {
        Vector2f dir = new Vector2f(gameObject.transform.position).sub(fistGO.transform.position);

        float distance = new Vector2f(dir).absolute().length();

        if (distance == 0f || distance < new Vector2f(dir).normalize().mul(fistSpeed * retractingSpeedMultiplier * dt).absolute().length()) {

            targetPos = null;
            fistGO.destroy();
            attackState = INACTIVE;
            isLastAttackPerformed = true;
            targetGameobject = null;
            currentDestination = null;
        } else {
            fistGO.transform.position.add(new Vector2f(dir).normalize().mul(fistSpeed * retractingSpeedMultiplier * dt));
        }

    }

    @Override
    public void onMousePress(int button) {
        isAttemptingToAttack = true;
    }

    @Override
    public void onMouseRelease(int button) {
        isAttemptingToAttack = false;
    }

    enum AttackType {
        STRAIGHT,
        ARC_LEFT,
        ARC_RIGHT;
    }

    protected enum AttackState {
        INACTIVE,
        MOVING_TO_TARGET,
        RETRACTING;
    }
}
