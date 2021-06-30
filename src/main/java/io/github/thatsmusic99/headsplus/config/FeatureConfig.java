package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;

public abstract class FeatureConfig extends HPConfig {

    public FeatureConfig(@NotNull String name)  {
        super(name);
    }

    public abstract boolean shouldLoad();
}
