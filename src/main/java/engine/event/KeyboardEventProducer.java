package engine.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface KeyboardEventProducer {

    Map<Integer, List<KeyboardEventListener>> pressListeners = new HashMap<>();

    Map<Integer, List<KeyboardEventListener>> releaseListeners = new HashMap<>();

    static void listenToKeyboardPress(int key, KeyboardEventListener i) {
        pressListeners.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
    }

    static void listenToKeyboardRelease(int key, KeyboardEventListener i) {
        releaseListeners.computeIfAbsent(key, k -> new ArrayList<>()).add(i);
    }

    static void clearListeners(){
        pressListeners.clear();
        releaseListeners.clear();
    }
}
