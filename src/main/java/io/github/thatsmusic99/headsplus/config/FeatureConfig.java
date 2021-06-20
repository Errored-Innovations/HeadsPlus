package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;

public abstract class FeatureConfig extends CMFile {

    public FeatureConfig(String name) {
        super(HeadsPlus.get(), name);
    }

    public abstract boolean shouldLoad();
}
