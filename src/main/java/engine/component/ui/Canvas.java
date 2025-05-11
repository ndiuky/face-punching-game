package engine.component.ui;

import engine.component.Component;

import java.util.ArrayList;
import java.util.List;

//TODO: вполне возможно что канвас не обязан быть компонентом
public class Canvas extends Component {

    public List<UiElement> uiElements = new ArrayList<>();

    @Override
    public void update(float dt) {
        for (int i = 0; i < uiElements.size(); i++) {
            // TODO: каждый кадр КАЖДЫЙ элемент обновляться НЕ ДОЛЖЕН!!!! пиздецки сильно влияет на производительность. Как минимум - юзать dirtyFlag, но в идеале - обновление только при получении соответствующего ивента
            uiElements.get(i).updateInternal();
        }
    }
}
