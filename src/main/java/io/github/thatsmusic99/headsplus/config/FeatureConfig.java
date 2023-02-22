package io.github.thatsmusic99.headsplus.config;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class FeatureConfig extends HPConfig {

    private boolean loaded = false;

    public FeatureConfig(@NotNull String name) throws IOException, IllegalAccessException {
        super(name);
    }

    public abstract boolean shouldLoad();

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
