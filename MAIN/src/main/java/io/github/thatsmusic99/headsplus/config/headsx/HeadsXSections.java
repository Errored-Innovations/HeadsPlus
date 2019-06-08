package io.github.thatsmusic99.headsplus.config.headsx;

public enum HeadsXSections {

    ALPHABET("alphabet", "&8[&6Alphabet&8]", "HP#wood_a", 0.0),
    ANIMALS("animals", "&8[&aAnimals&8]", "HP#elephant", 0.0),
    DECORATION("decoration", "&8[&eDecoration&8]", "HP#paper_lantern", 0.0),
    FOOD_AND_DRINK("food_and_drink", "&8[&6Food &7and &9Drink&8]", "HP#pepsi", 0.0),
    LOGOS("logos", "&8[&cLogos&8]", "HP#youtube", 0.0),
    PLANTS("plants", "&8[&aPlants&8]", "HP#white_flower_bush", 1.0),
    COLOURS("colors", "&8[&4C&6o&el&ao&br&ds&8]", "HP#rainbow", 1.2),
    EMOTES("emotes", "&8[&eEmotes&8]", "HP#e_wink", 1.3),
    BLOCKS("blocks", "&8[&9Blocks&8]", "HP#oak_log", 1.6);

    public final String let;
    public final String dn;
    public final String tx;
    public final double d;

    HeadsXSections(String l, String dn, String t, double d) {
        this.let = l;
        this.dn = dn;
        this.tx = t;
        this.d = d;
    }
}
