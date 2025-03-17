package engine.util.listener;

import engine.event.KeyboardEventListener;
import engine.event.KeyboardEventProducer;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener implements KeyboardEventProducer {

    private static KeyListener instance;

    private final boolean[] keyPressed = new boolean[350];

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }

        return instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;

            List<KeyboardEventListener> listeners = pressListeners.get(key);
            if (listeners != null) {
                for (KeyboardEventListener listener : listeners) {
                    listener.onKeyPress(key);
                }
            }
        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;

            List<KeyboardEventListener> listeners = releaseListeners.get(key);
            if (listeners != null) {
                for (KeyboardEventListener listener : listeners) {
                    listener.onKeyRelease(key);
                }
            }
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }
}