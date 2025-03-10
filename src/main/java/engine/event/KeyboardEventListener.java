package engine.event;

public interface KeyboardEventListener {

    void onKeyPress(final int key);

    void onKeyRelease(final int key);
}
