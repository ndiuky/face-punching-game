import engine.render.Window;
import engine.util.EnginePreferences;

// TODO: форсировать запуск с ZGC
// TODO: переделать сейвфайл на .penis формат
public class Main {
    public static void main(String[] args) throws Exception {
        EnginePreferences.applyConfigurationFromConfigFile();

        Window.get().run();
    }
}