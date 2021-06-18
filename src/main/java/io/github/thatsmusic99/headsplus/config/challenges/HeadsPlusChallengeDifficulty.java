package io.github.thatsmusic99.headsplus.config.challenges;

import org.bukkit.DyeColor;
import org.bukkit.Material;

public enum HeadsPlusChallengeDifficulty {
    
    EASY("easy", "&8[&a&lEasy&8]", "GREEN_TERRACOTTA", 20, 1, 1),
    EASY_MEDIUM("easy_medium", "&8[&a&lEasy&8-&6&lMedium&8]", "LIME_TERRACOTTA", 22, 1, 2),
    MEDIUM("medium","&8[&6&lMedium&8]", "YELLOW_TERRACOTTA", 24, 1, 3),
    MEDIUM_HARD("medium_hard", "&8[&6&lMedium&8-&c&lHard&8]", "ORANGE_TERRACOTTA", 30, 1, 4),
    HARD("hard", "&8[&c&lHard&8]", "RED_TERRACOTTA", 32, 1, 5),
    TEDIOUS("tedious", "&8[&c&lTedious&8]", "BLUE_TERRACOTTA", 20, 2, 6),
    TEDIOUS_PAINFUL("tedious_painful", "&8[&c&lTedious&8-&5&lPainful&8]", "PURPLE_TERRACOTTA", 22, 2, 7),
    PAINFUL("painful", "&8[&5&lPainful&8]", "MAGENTA_TERRACOTTA",  24, 2, 8),
    PAINFUL_DEADLY("painful_deadly", "&8[&5Painful&8-&4Deadly&8]", "PINK_TERRACOTTA", 30, 2, 9),
    DEADLY("deadly", "&8[&4Deadly&8]", "RED_TERRACOTTA", 32, 2, 10);

    public String key;
    String displayName;
    public String material;
    public int i;
    public int p;
    public int page;
    public int min;

    HeadsPlusChallengeDifficulty(String key, String dn, String material, int i, int page, int min) {
        this.key = key;
        this.displayName = dn;
        this.material = material;
        this.i = i;
        this.page = page;
        this.min = min;
    }
}
