import engine.render.Window;
import engine.util.EnginePreferences;
import util.Utils;

import java.util.Arrays;
import java.util.Map;

// TODO: убрать все transient
// TODO: удалить gson
// TODO: переделать сейвфайл на .penis формат
public class Main {
    public static void main(String[] args) throws Exception{
        EnginePreferences.applyConfigurationFromConfigFile();

        Window.get().run();
    }
}
