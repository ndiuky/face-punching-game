import engine.render.Window;
import engine.util.EnginePreferences;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (Arrays.asList(args).contains("-devMode")){
            EnginePreferences.enableDevMode();
        }

        Window.get().run();
    }
}
