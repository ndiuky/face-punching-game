package component.object;

import component.Item;
import engine.component.CSpriteRenderer;
import engine.component.Component;
import engine.component.ui.CPositionText;
import engine.component.ui.CTextRenderer;
import engine.object.Sprite;
import engine.render.Window;
import engine.text.Text;
import engine.util.Alignment;
import engine.util.AssetPool;
import engine.util.Color;
import engine.util.Interactable;
import engine.util.listener.MouseListener;
import org.joml.Vector2f;
import util.InteractionType;
import util.LootTable;

import java.util.List;
import java.util.Random;

import static util.InteractionType.ATTACK;

public class Stone extends Component implements Interactable {

    private int hp = 10;

    CTextRenderer textRenderer;

    private LootTable lootTable = LootTable.of(
            Item.generateFromTexture("asset/texture/nut-0.png"),
            Item.generateFromTexture("asset/texture/nut-1.png"),
            Item.generateFromTexture("asset/texture/screw-0.png"),
            Item.generateFromTexture("asset/texture/screw-1.png")
    );

    @Override
    public void start() {
        textRenderer = new CTextRenderer().setText(new Text("").setColor(Color.TRANSLUCENT)).setAlignment(Alignment.BOTTOM_MIDDLE);
        textRenderer.getText().setSizeModifier(0.5f);
        gameObject.addComponent(textRenderer);

        gameObject.addComponent(new CPositionText());

        gameObject.addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/stone.png"))));
    }

    Random random = new Random();

    @Override
    public void update(float dt) {
        textRenderer.setTextContent("gId: " + gameObject.getUid() + ", cId: " + getUid());
        textRenderer.setTextContent((int) gameObject.transform.position.x + ", " + (int) gameObject.transform.position.y);
        if (MouseListener.mouseButtonDown(1)) {
            gameObject.transform.position.add(new Vector2f(random.nextInt(-20, 21) * dt, random.nextInt(-20, 21) * dt));
        }
    }

    private void reduceHp(int reduceBy) {
        hp -= reduceBy;
        if (hp <= 0) {
            destroyStone();
        }
    }

    private void destroyStone() {
        for (Item item : lootTable.getContent()) {
            item.gameObject.transform.position = new Vector2f(gameObject.transform.position);
            item.applyInitialImpulse(new Vector2f(random.nextFloat(-1, 1), random.nextFloat(-1, 1)));
            Window.getCurrentScene().addGameObjectToScene(item.gameObject);
        }
        gameObject.destroy();
    }

    @Override
    public List<InteractionType> getInteractionTypes() {
        return List.of(ATTACK);
    }

    @Override
    public void onInteraction(InteractionType interactionType) {
        if (interactionType == ATTACK) {
            reduceHp(3);
        }
    }
}
