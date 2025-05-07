package engine.util;

import util.Utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Variables of this class should not be changed from code in runtime, use<code>engineConfig.penis<code/> file instead
 */
public class EnginePreferences {

    // Engine constants, shouldn't be changed even from config file
    public static final String CONFIG_FILE_RELATIVE_PATH = "engineConfig.penis";
    public static final int DEFAULT_QUAD_VERTICES_AMOUNT = 6;
    public static final char CONFIG_FILE_COMMENT_CHAR = '$';
    public static final int EBO_QUAD_VERTICES_AMOUNT = 4;
    public static final int MAX_BATCH_SIZE = 1024;

    public static boolean IS_DEBUG_MENU_ACTIVE_BY_DEFAULT = false;
    public static boolean ENABLE_SMALLER_WINDOWED_SIZE = false;
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH = 32;
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID = 16;
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_LOW = 8;
    public static String SAVE_FILE_NAME = "saveFile.txt";
    public static boolean FULLSCREEN_BY_DEFAULT = true;
    public static boolean ENABLE_DATA_LOADING = true;
    public static boolean ENABLE_DATA_SAVING = true;
    public static int CREATE_WINDOW_ON_MONITOR = 0;
    public static float TEXT_BASE_SIZE = 0.01f;
    public static boolean ENABLE_VSYNK = true;
    public static int FONT_RESOLUTION = 64;
    public static int GRID_SIZE = 32;

    private EnginePreferences() {
    }

    @SuppressWarnings("java:S3011")
    public static void applyConfigurationFromConfigFile() throws Exception {
        Map<String, String> config = Utils.loadPenisFile(CONFIG_FILE_RELATIVE_PATH)
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFirst()));

        config.forEach((k, v) -> {
            if (v.equals("default") || v.isBlank()) {
                return;
            }

            try {
                final Field declaredField = EnginePreferences.class.getDeclaredField(k);
                if (declaredField.getType() == byte.class) {
                    declaredField.setByte(null, Byte.parseByte(v));
                } else if (declaredField.getType() == short.class) {
                    declaredField.setShort(null, Short.parseShort(v));
                } else if (declaredField.getType() == int.class) {
                    declaredField.setInt(null, Integer.parseInt(v));
                } else if (declaredField.getType() == long.class) {
                    declaredField.setLong(null, Long.parseLong(v));
                } else if (declaredField.getType() == char.class) {
                    declaredField.setChar(null, v.charAt(0));
                } else if (declaredField.getType() == boolean.class) {
                    declaredField.setBoolean(null, Boolean.parseBoolean(v));
                } else if (declaredField.getType() == float.class) {
                    declaredField.setFloat(null, Float.parseFloat(v));
                } else if (declaredField.getType() == double.class) {
                    declaredField.setDouble(null, Double.parseDouble(v));
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
