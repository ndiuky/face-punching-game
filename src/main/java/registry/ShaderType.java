package registry;

public enum ShaderType {

    DEFAULT("default"),
    DEBUG_LINE("debugLine"),
    PICKING("pickingShader"),
    TEXT("textShader"),
    UI_TEXTURE("uiTextureShader"),
    UI_TEXT("uiTextShader"),
    ;

    public final String name;

    ShaderType(final String name) {
        this.name = name;
    }
}
