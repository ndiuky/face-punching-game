package engine.component.ui;

import engine.component.Component;

import java.util.ArrayList;
import java.util.List;

//TODO: текущий план: Renderer будет содержать лист канвасов для рендеринга, и вызывать метод обновления в нём каждый кадр. из канваса берутся uiElements, и из каждого берутся спрайты для рендеринга
//TODO: вполне возможно что канвас не обязан быть компонентом
public class Canvas extends Component {
    private List<UiElement> uiElements = new ArrayList<>();

    @Override
    public void update(float dt) {
        for (int i = 0; i < uiElements.size(); i++) {
            uiElements.get(i).update();
        }
//        for (UiElement uiElement : uiElements) {
//            uiElement.update();
//        }
    }

    public List<UiElement> getUiElements() {
        return List.copyOf(uiElements);
    }

    public void addUiElement(UiElement uiElement) {
        uiElements.add(uiElement);
    }
}
