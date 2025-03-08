package engine.event;

public interface KeyboardEventListener {

    void OneKeyPress(final int key);

    void onKeyRelease(final int key);
}
