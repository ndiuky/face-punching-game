package engine.component.ui;

import engine.object.Sprite;
import engine.render.Texture;

public class UiSprite extends UiElement {

    private Sprite sprite;

    public UiSprite(Canvas canvas, Sprite sprite){
        super(canvas);

        this.sprite = sprite;
        uiSprites.add(this);
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    @Override
    protected void update() {
        //TODO: change sprite if needed
    }
}
