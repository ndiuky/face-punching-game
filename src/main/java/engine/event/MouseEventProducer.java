package engine.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MouseEventProducer {

    Map<Integer, List<MouseEventListener>> pressListeners = new HashMap<>();

    Map<Integer, List<MouseEventListener>> releaseListeners = new HashMap<>();

    static void listenToMousePress(int button, MouseEventListener i) {
        pressListeners.computeIfAbsent(button, k -> new ArrayList<>()).add(i);
    }

    static void listenToMouseRelease(int button, MouseEventListener i) {
        releaseListeners.computeIfAbsent(button, k -> new ArrayList<>()).add(i);
    }
}
