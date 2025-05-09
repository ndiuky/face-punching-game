package registry;

public enum ShaderType {

    DEFAULT("default"),
    DEBUG_LINE("debugLine"),
    PICKING("pickingShader"),
    FONT("fontShader"),
    UI("uiShader"),
    ;

    public final String name;

    ShaderType(final String name) {
        this.name = name;
    }
}
