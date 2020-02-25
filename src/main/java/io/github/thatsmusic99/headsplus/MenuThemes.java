package io.github.thatsmusic99.headsplus;

public enum MenuThemes {

    CLASSIC("DARK_BLUE", "GOLD", "GRAY", "DARK_AQUA"),
    COAL("BLACK", "GREY", "DARK_GREY", "DARK_GREY"),
    IRON("GRAY", "WHITE", "WHITE", "GRAY"),
    RUBY("DARK_RED", "RED", "RED", "DARK_RED"),
    DIAMOND("DARK_AQUA", "AQUA", "GRAY", "DARK_AQUA"),
    EMERALD("DARK_GREEN", "GREEN", "GREEN", "DARK_GREEN"),
    AMETHYST("DARK_PURPLE", "WHITE", "LIGHT_PURPLE", "PURPLE"),
    OCEAN("GREEN", "AQUA", "WHITE", "GREEN"),
    BEEHIVE("GRAY", "GOLD", "GOLD", "GRAY"),
    NETHER("DARK_RED", "GOLD", "RED", "DARK_RED"),
    SAPPHIRE("DARK_BLUE", "BLUE", "DARK_AQUA", "DARK_BLUE");

    public final String c1;
    public final String c2;
    public final String c3;
    public final String c4;

    MenuThemes(String c1, String c2, String c3, String c4) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;
    }
}
