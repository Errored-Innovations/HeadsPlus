package io.github.thatsmusic99.headsplus.api;

public class BaseLevel extends Level {

    private final double addedVersion;

    public BaseLevel(String configName, String displayName, int requiredXP, double addedVersion) {
        super(configName, displayName, requiredXP, null);
        this.addedVersion = addedVersion;
    }
    
    public double getAddedVersion() {
        return addedVersion;
    }

}
