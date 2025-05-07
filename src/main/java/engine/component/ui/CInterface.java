package engine.component.ui;

import engine.component.Component;

import java.util.ArrayList;
import java.util.List;

//TODO: сделай удобную систему для UI
public class CInterface extends Component {

    private final List<UiElement> uiElements = new ArrayList<>();

    @Override
    public void update(float dt) {

    }

    public CInterface addElement(UiElement element){
        uiElements.add(element);
        return this;
    }

    public CInterface addElements(List<UiElement>elements){
        uiElements.addAll(elements);
        return this;
    }
}
