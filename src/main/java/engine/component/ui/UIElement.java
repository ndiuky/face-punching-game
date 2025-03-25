package engine.component.ui;

import engine.util.Alignment;

public interface UIElement<T> {

    Alignment getAlignment();

    T setAlignment(Alignment alignment);

    void updateAlignment();
}
