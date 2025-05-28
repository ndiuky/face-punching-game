package component.object;

import engine.component.CSpriteRenderer;
import engine.component.ui.CTextRenderer;
import engine.object.Sprite;
import engine.render.Texture;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.AssetPool;
import engine.util.Color;
import registry.InteractionType;
import registry.TextureType;

// TODO: это тестовый класс, чтобы проверить несколько рандомных вещей. В целом можно под что-то более интересное переделать
public class OpenableTest extends InteractableComponent {

    private final CTextRenderer openedStateText;

    private boolean isOpened = false;

    private CSpriteRenderer spriteRenderer;

    private Sprite openedSprite = new Sprite(AssetPool.getTexture(TextureType.CHEST_OPENED));

    private Sprite closedSprite = new Sprite(AssetPool.getTexture(TextureType.CHEST_CLOSED));

    public OpenableTest() {
        Text text = new Text("thing is closed!").setSizeModifier(0.5f);
        text.alignment = Alignment.BOTTOM_CENTER;
        text.color = Color.RED;
        openedStateText = new CTextRenderer(text);
    }

    @Override
    public void start() {
        spriteRenderer = new CSpriteRenderer(closedSprite);
        gameObject
                .addComponent(spriteRenderer)
                .addComponent(openedStateText);

        initInteractableComponent();
    }

    @Override
    public void update(final float dt) {
        playAnimation(dt);
    }

    @Override
    public InteractionType[] getAllowedInteractionTypes() {
        return new InteractionType[]{InteractionType.USE};
    }

    @Override
    protected void afterInteraction(final InteractionType interactionType) {
        if (InteractionType.USE.equals(interactionType)) {
            isOpened = !isOpened;

            openedStateText.setTextContent(isOpened ? "thing is opened!" : "thing is closed!");
            openedStateText.getText().color = isOpened ? Color.GREEN : Color.RED;
            spriteRenderer.setSprite(isOpened ? openedSprite : closedSprite);
        }
    }
}
