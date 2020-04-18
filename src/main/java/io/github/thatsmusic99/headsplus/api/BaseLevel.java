package io.github.thatsmusic99.headsplus.api;

public class BaseLevel {

    private final String configName;
    private final String displayName;
    private final int requiredXP;
    private final double addedVersion;

    public BaseLevel(String configName, String displayName, int requiredXP, double addedVersion) {
        this.configName = configName;
        this.displayName = displayName;
        this.requiredXP = requiredXP;
        this.addedVersion = addedVersion;
    }

    // Y
    public String getConfigName() {
        return configName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getRequiredXP() {
        return requiredXP;
    }

    public double getAddedVersion() {
        return addedVersion;
    }

}
