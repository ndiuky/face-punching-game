package engine.util;

import java.util.Map;

public class EnginePreferences {

    public static int DEFAULT_QUAD_VERTICES_AMOUNT = 6;
    public static int EBO_QUAD_VERTICES_AMOUNT = 4;
    public static String SAVE_FILE_NAME = "saveFile.txt";
    public static int GRID_SIZE = 32;
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_HIGH = 32;
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_MID = 16;
    public static int DEBUG_CIRCLE_SEGMENTS_AMOUNT_LOW = 8;
    //true to enable deserialization
    public static boolean ENABLE_DATA_LOADING = true;
    //true to enable serialization
    public static boolean ENABLE_DATA_SAVING = true;
    public static boolean ENABLE_VSYNK = true;
    public static boolean FULLSCREEN_BY_DEFAULT = true;
    public static boolean ENABLE_SMALLER_WINDOWED_SIZE = false;
    public static boolean IS_DEBUG_MENU_ACTIVE_BY_DEFAULT = false;
    public static int FONT_RESOLUTION = 64;
    public static float TEXT_BASE_SIZE = 0.01f;
    public static int MAX_BATCH_SIZE = 1024;

    private static final Map<String, Object> DEV_MODE_MAPPING = Map.ofEntries(
            Map.entry("ENABLE_DATA_LOADING", false),
            Map.entry("ENABLE_DATA_SAVING", false),
            Map.entry("ENABLE_VSYNK", false),
            Map.entry("FULLSCREEN_BY_DEFAULT", false),
            Map.entry("IS_DEBUG_MENU_ACTIVE_BY_DEFAULT", true)
    );


    public static void enableDevMode() {
        DEV_MODE_MAPPING.forEach((fieldName, fieldValue) -> {
            try {
                EnginePreferences.class.getDeclaredField(fieldName).set(null, fieldValue);
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                throw new RuntimeException("Exception during dev mode enabling attempt (on variable \"" + fieldName + "\"): " + ex.getMessage());
            }
        });
    }
}
