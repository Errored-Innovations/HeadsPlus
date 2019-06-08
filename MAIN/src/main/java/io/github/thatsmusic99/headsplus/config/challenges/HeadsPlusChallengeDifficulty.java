package io.github.thatsmusic99.headsplus.config.challenges;

import org.bukkit.DyeColor;

public enum HeadsPlusChallengeDifficulty {
    
    EASY("easy", DyeColor.GREEN, 20, 1),
    EASY_MEDIUM("easy_medium", DyeColor.LIME, 22, 1),
    MEDIUM("medium", DyeColor.YELLOW, 24, 1),
    MEDIUM_HARD("medium_hard", DyeColor.ORANGE, 30, 1),
    HARD("hard", DyeColor.RED, 32, 1),
    TEDIOUS("tedious", DyeColor.BLUE, 20, 2),
    TEDIOUS_PAINFUL("tedious_painful", DyeColor.PURPLE, 22, 2),
    PAINFUL("painful", DyeColor.MAGENTA, 24, 2),
    PAINFUL_DEADLY("painful_deadly", DyeColor.PINK, 30, 2),
    DEADLY("deadly", DyeColor.RED, 32, 2);

    public String key;
    public DyeColor color;
    public int i;
    public int p;
    public int page;

    HeadsPlusChallengeDifficulty(String key, DyeColor color, int i, int page) {
        this.key = key;
        this.color = color;
        this.i = i;
        this.page = page;
    }
}
