package component.object;

import component.ItemDrop;
import engine.render.Window;
import org.joml.Vector2f;
import util.LootTable;

import static engine.util.EngineUtils.RANDOM;

public abstract class BreakableComponent extends InteractableComponent {

    public int maxHp = 1;

    private int hp = maxHp;

    private LootTable lootTable;

    protected void afterBreak() {
        // override on demand
    }

    protected final void initBreakableComponent(int maxHp, LootTable lootTable) {
        this.lootTable = lootTable;
        this.maxHp = maxHp;
        hp = maxHp;

        initInteractableComponent();
    }


    protected final void reduceHp(int reduceBy) {
        hp -= reduceBy;
        if (hp <= 0) {
            baseOnBreak();
        }
    }

    public final void baseOnBreak() {
        for (ItemDrop itemDrop : lootTable.getContentDropped()) {
            itemDrop.gameObject.transform.position = new Vector2f(gameObject.transform.position);
            itemDrop.applyInitialImpulse(new Vector2f(RANDOM.nextFloat(-1, 1), RANDOM.nextFloat(-1, 1)));
            Window.currentScene.addGameObjectToScene(itemDrop.gameObject);
        }
        gameObject.destroy();

        afterBreak();
    }
}
