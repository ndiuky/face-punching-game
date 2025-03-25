package component.object;

import component.Item;
import engine.component.CSpriteRenderer;
import engine.component.Component;
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
        gameObject.addComponent(textRenderer);

        gameObject.addComponent(new CSpriteRenderer().setSprite(new Sprite().setTexture(AssetPool.getTexture("asset/texture/stone.png"))));
    }

    @Override
    public void update(float dt) {
        textRenderer.setTextContent("id: " + gameObject.getUid());
        if (MouseListener.mouseButtonDown(1)) {
            gameObject.transform.position.add(new Vector2f(new Random().nextInt(-2, 3), new Random().nextInt(-2, 3)));
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
            item.applyInitialImpulse(new Vector2f(new Random().nextInt(-50, 50), new Random().nextInt(-50, 50)));
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

            System.out.println("attacked gameobject with id = " + gameObject.getUid());
        }
    }
}
