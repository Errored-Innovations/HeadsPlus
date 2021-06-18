package io.github.thatsmusic99.headsplus.config;

public class ConfigHeadsSelector extends FeatureConfig {

    public ConfigHeadsSelector() {
        super("heads-selector");
    }

    @Override
    public boolean shouldLoad() {
        return false;
    }

    @Override
    public void loadDefaults() {

    }
}
