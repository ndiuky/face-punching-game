package component.object;

import engine.component.CSpriteRenderer;
import engine.component.Component;
import engine.util.Color;
import org.joml.Vector2f;
import org.joml.Vector4f;
import registry.InteractionType;

import java.util.ArrayList;
import java.util.List;

import static component.object.InteractableComponent.AnimationState.ALLOWED;
import static component.object.InteractableComponent.AnimationState.DENIED;
import static component.object.InteractableComponent.AnimationState.NONE;
import static engine.util.EngineUtils.inverseLerpUnclamped;
import static util.AnimationUtils.MAX_DEGREES_ATTACK_ANIMATION_DELTA;
import static util.AnimationUtils.MAX_SCALE_INTERACTION_DENIED_ANIMATION_DELTA;
import static util.AnimationUtils.MAX_Y_ATTACK_ANIMATION_DELTA;
import static util.AnimationUtils.easeOutElastic;
import static util.AnimationUtils.easeOutExp;

public abstract class InteractableComponent extends Component {

    private final List<CSpriteRenderer> gameobjectSpriteRenderers = new ArrayList<>();

    protected float animationDuration = 0.5f;

    protected AnimationState currAnimationState = AnimationState.NONE;

    protected Vector2f originalScale;

    protected float originalRotation;

    protected Vector2f originalPosition;

    protected InteractionType lastInteractionType;

    private float currentInteractionAnimationTime = 0;

    public abstract InteractionType[] getAllowedInteractionTypes();

    // TODO: override this method in BreakableComponent instad of making another one
    protected abstract void afterInteraction(InteractionType interactionType);

    protected final void initInteractableComponent() {
        resetOriginalTransform();
    }

    public final void baseOnInteraction(InteractionType interactionType) {
        currAnimationState = getAnimationStateByInteractionType(interactionType);
        currentInteractionAnimationTime = 0;
        resetOriginalTransform();
        gameobjectSpriteRenderers.clear();
        gameobjectSpriteRenderers.addAll(gameObject.getComponents(CSpriteRenderer.class));

        lastInteractionType = interactionType;

        afterInteraction(interactionType);
    }

    private AnimationState getAnimationStateByInteractionType(InteractionType interactionType) {
        for (InteractionType allowedInteractionType : getAllowedInteractionTypes()) {
            if (allowedInteractionType.equals(interactionType)) {
                return ALLOWED;
            }
        }

        return AnimationState.DENIED;
    }

    protected final void playAnimation(float dt) {
        if (NONE.equals(currAnimationState)) {
            return;
        }

        currentInteractionAnimationTime += dt;
        float lerpResult = inverseLerpUnclamped(0, animationDuration, currentInteractionAnimationTime);
        if (lerpResult >= 1) {
            resetAnimationParameters();

            return;
        }

        // TODO: несколько анимаций тут - простая интерполяция между 0 и 1, из-за чего начальная/конечная точка объекта может сильно смещаться. Проблема в основном в формулах AnimationUtils, мб стоит сделать метод-хелпер, который этот промежуток перетащит в промежуток [-0.5; 0.5]
        if (ALLOWED.equals(currAnimationState)) {
            switch (lastInteractionType) {
                case ATTACK -> {
                    gameObject.transform.scale = new Vector2f(originalScale.x, (originalScale.y + easeOutExp(lerpResult) * MAX_Y_ATTACK_ANIMATION_DELTA));
                    gameObject.transform.rotation = originalRotation + (1 - easeOutElastic(lerpResult)) * MAX_DEGREES_ATTACK_ANIMATION_DELTA;
                }
                case USE -> {
                    float v = easeOutExp(lerpResult) * MAX_SCALE_INTERACTION_DENIED_ANIMATION_DELTA;
                    gameObject.transform.scale = new Vector2f(originalScale.x - v, originalScale.y - v);
                }
                case null,
                     default -> resetAnimationParameters();
            }
        } else if (DENIED.equals(currAnimationState)) {
            gameObject.transform.position = new Vector2f(originalPosition.x + 0.2f * (1 - easeOutElastic(lerpResult)), originalPosition.y);

            for (CSpriteRenderer csr : gameobjectSpriteRenderers) {
                csr.setColor(new Vector4f(1, lerpResult, lerpResult, 1));
            }
        }
    }

    private void resetAnimationParameters() {
        for (CSpriteRenderer csr : gameobjectSpriteRenderers) {
            csr.setColor(Color.WHITE);
        }
        gameObject.transform.scale = new Vector2f(originalScale);
        gameObject.transform.rotation = originalRotation;
        gameObject.transform.position = originalPosition;

        currentInteractionAnimationTime = 0;
        currAnimationState = NONE;
    }

    private void resetOriginalTransform() {
        this.originalScale = gameObject.transform.scale;
        this.originalRotation = gameObject.transform.rotation;
        this.originalPosition = gameObject.transform.position;
    }

    public enum AnimationState {
        ALLOWED,
        DENIED,
        NONE,
        ;
    }
}
