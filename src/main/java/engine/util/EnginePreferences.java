package engine.util;

import util.Utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EnginePreferences {

    // TODO: убрать возможность менять эти переменные
    // Engine constants, do not change
    public static final String CONFIG_FILE_RELATIVE_PATH = "engineConfig.penis";
    public static final char CONFIG_FILE_COMMENT_CHAR = '$';
    public static final int DEFAULT_QUAD_VERTICES_AMOUNT = 6;
    public static final int EBO_QUAD_VERTICES_AMOUNT = 4;
    public static final int MAX_BATCH_SIZE = 1024;

    //true to enable deserialization
    public static boolean ENABLE_DATA_LOADING = true;
    //true to enable serialization
    public static boolean ENABLE_DATA_SAVING = true;
    public static String SAVE_FILE_NAME = "saveFile.txt";
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH = 32;
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID = 16;
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_LOW = 8;
    public static int GRID_SIZE = 32;
    public static boolean ENABLE_VSYNK = true;
    public static boolean FULLSCREEN_BY_DEFAULT = true;
    public static boolean ENABLE_SMALLER_WINDOWED_SIZE = false;
    public static boolean IS_DEBUG_MENU_ACTIVE_BY_DEFAULT = false;
    public static int FONT_RESOLUTION = 64;
    public static float TEXT_BASE_SIZE = 0.01f;
    public static int CREATE_WINDOW_ON_MONITOR = 0;

    public static void applyConfigurationFromConfigFile() throws Exception {
        Map<String, String> config = Utils.loadPenisFile(CONFIG_FILE_RELATIVE_PATH);

        config.forEach((k, v) -> {
            if (v.equals("default") || v.isBlank()) {
                return;
            }

            try {
                final Field declaredField = EnginePreferences.class.getDeclaredField(k);
                if (declaredField.getType() == byte.class) {
                    declaredField.setByte(null, Byte.valueOf(v));
                } else if (declaredField.getType() == short.class) {
                    declaredField.setShort(null, Short.valueOf(v));
                } else if (declaredField.getType() == int.class) {
                    declaredField.setInt(null, Integer.valueOf(v));
                } else if (declaredField.getType() == long.class) {
                    declaredField.setLong(null, Long.valueOf(v));
                } else if (declaredField.getType() == char.class) {
                    declaredField.setChar(null, v.charAt(0));
                } else if (declaredField.getType() == boolean.class) {
                    declaredField.setBoolean(null, Boolean.valueOf(v));
                } else if (declaredField.getType() == float.class) {
                    declaredField.setFloat(null, Float.valueOf(v));
                } else if (declaredField.getType() == double.class) {
                    declaredField.setDouble(null, Double.valueOf(v));
                } else {
                    // setting String if noting else found
                    declaredField.set(null, v);
                }

            } catch (Exception ex) {
                throw new RuntimeException("Exception during dev mode enabling attempt (on variable \"" + k + "\" with value \"" + v + "\"): " + ex.getMessage());
            }
        });
    }
}
