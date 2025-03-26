package engine.component.ui;

import engine.component.Component;

import java.util.ArrayList;
import java.util.List;

//TODO: сделай удобную систему для UI
public class CInterface extends Component {

    //TODO: обновлять эти переменные в update каждый кадр
    private static float worldLeftX, worldRightX, worldBottomY, worldUpperY;

    private final List<UIElement> uiElements = new ArrayList<>();

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    public CInterface addElement(UIElement element){
        uiElements.add(element);
        return this;
    }

    public CInterface addElements(List<UIElement>elements){
        uiElements.addAll(elements);
        return this;
    }
}
