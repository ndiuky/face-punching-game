package engine.util.listener;

import engine.event.KeyboardEventListener;
import engine.event.KeyboardEventProducer;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener implements KeyboardEventProducer {

    // TODO: есть идея переделать все клавиши на массив byte элементов, и отображать состояние клавиши в текущий кадр, изменяя конкретный бит этого элемента. Например, первый бит - KEY_DOWN, по умолчанию = 0. В кадр нажатия клавиши этот бит меняется на 1, а в следующем кадре меняется обратно на 0. Второй бит - KEY_UP. Также по умолчанию = 0, и меняется на 1 в кадр, когда клавиша отжимается. Третий бит - нажата ли клавиша в текущий кадр. Нужны константы, определяющие за что отвечает каждый бит, и можно будет просто брать элемент из этого массива по нужному индексу, затем через & и определённую константу выяснять, равен ли нужный бит 1. Если равен - условие выполнено. Тоже самое можно сделать и в MouseListener
    private static final boolean[] keyPressed = new boolean[350];

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            keyPressed[key] = true;

            List<KeyboardEventListener> listeners = pressListeners.get(key);
            if (listeners != null) {
                for (KeyboardEventListener listener : listeners) {
                    listener.onKeyPress(key);
                }
            }
        } else if (action == GLFW_RELEASE) {
            keyPressed[key] = false;

            List<KeyboardEventListener> listeners = releaseListeners.get(key);
            if (listeners != null) {
                for (KeyboardEventListener listener : listeners) {
                    listener.onKeyRelease(key);
                }
            }
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return keyPressed[keyCode];
    }
}