package util;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

//TODO: нужен .penis файл, который будет содержать в себе все предустановки игрока. Например уровень графики, переназначение клавиш, настройки звука и т.п.. Затем загружать данные с этого json'а в необходимые классы
public enum KeyMap {

    MOVE_UP(GLFW_KEY_W),
    MOVE_LEFT(GLFW_KEY_A),
    MOVE_DOWN(GLFW_KEY_S),
    MOVE_RIGHT(GLFW_KEY_D),

    ;

    private final int value;

    KeyMap(int value) {
        this.value = value;
    }

    public int getMappedKey() {
        return KEY_MAP.get(value);
    }

    private static final Map<Integer, Integer> KEY_MAP = new HashMap<>();


}
