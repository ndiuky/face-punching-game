import engine.render.Window;
import engine.util.EnginePreferences;

// TODO: форсировать запуск с ZGC
// TODO: переделать сейвфайл на .penis формат
//TODO: все методы должны возвращать оригинальные объекты. А мутировать их или нет - решает уже вызывающая сторона
public class Main {
    public static void main(String[] args) throws Exception {
        EnginePreferences.applyConfigurationFromConfigFile();

        Window.get().run();
    }
}