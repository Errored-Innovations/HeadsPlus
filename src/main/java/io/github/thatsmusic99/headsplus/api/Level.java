package io.github.thatsmusic99.headsplus.api;

/**
 * Represents a level. This is a cosmetic feature that players can aim for.
 */
public class Level {

    private final String configName;
    private final String displayName;
    private final int requiredXP;
    private final Reward reward;

    public Level(String configName, String displayName, int requiredXP, Reward reward) {
        this.configName = configName;
        this.displayName = displayName;
        this.requiredXP = requiredXP;
        this.reward = reward;
    }

    public String getConfigName() {
        return configName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRequiredXP() {
        return requiredXP;
    }

    public boolean isrEnabled() {
        return reward != null;
    }

    public Reward getReward() {
        return reward;
    }
}
