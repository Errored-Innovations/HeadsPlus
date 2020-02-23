package io.github.thatsmusic99.headsplus.api;

public class BaseLevel {

    private String configName;
    private String displayName;
    private int requiredXP;
    private double addedVersion;

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
