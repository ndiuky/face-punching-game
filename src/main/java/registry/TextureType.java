package registry;

public enum TextureType {

    CHIP_SMALL("chip-small"),
    CHIP_SMALL_BACK("chip-small-back"),
    CHIP_SMALL_LOCKED("chip-small-locked"),
    CHEST_CLOSED("chest"),
    CHEST_OPENED("chest-opened"),
    DUMMY("dummy"),
    FIST("fist"),
    GRASS_0("grass-0"),
    NUT_0("nut-0"),
    NUT_1("nut-1"),
    SCREW_0("screw-0"),
    SCREW_1("screw-1"),
    STONE("stone"),
    TREE("tree"),
    PIXEL("pixel"),
    ATTACK_BUTTON("ui/attack-button"),
    BLOCK_BUTTON("ui/block-button"),
    BONUS_BUTTON("ui/bonus-button"),
    MOVE_BUTTON("ui/move-button"),
    ;

    public final String path;

    TextureType(final String path) {
        this.path = path;
    }
}
