package io.github.thatsmusic99.headsplus.config.defaults;

public enum HeadsXSections {

    ALPHABET("alphabet", "&8[&6Alphabet&8]", "HP#wood_a", 0.0),
    ANIMALS("animals", "&8[&aAnimals&8]", "HP#elephant", 0.0),
    DECORATION("decoration", "&8[&eDecoration&8]", "HP#paper_lantern", 0.0),
    FOOD_AND_DRINK("food_and_drink", "&8[&6Food &7and &9Drink&8]", "HP#pepsi", 0.0),
    LOGOS("logos", "&8[&cLogos&8]", "HP#youtube", 0.0),
    PLANTS("plants", "&8[&aPlants&8]", "HP#white_flower_bush", 1.0),
    COLOURS("colors", "&8[&4C&6o&el&ao&br&ds&8]", "HP#rainbow", 1.2),
    EMOTES("emotes", "&8[&eEmotes&8]", "HP#e_wink", 1.3),
    BLOCKS("blocks", "&8[&9Blocks&8]", "HP#oak_log", 1.6),
    PLAYERS("players", "&8[&6Players&8]", "HP#players_1", 2.5),
    MOBS("mobs", "&8[&cMobs&8]", "HP#evoker", 2.6),
    HUMANS("humans", "&8[&6Humans&8]", "HP#villager_plains", 2.9),
    GEMS("gems", "&8[&bGems&8]", "HP#amethyst", 3.0);

    public final String id;
    public final String displayName;
    public final String texture;
    public final double version;

    HeadsXSections(String id, String displayName, String texture, double version) {
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
        this.version = version;
    }
}
