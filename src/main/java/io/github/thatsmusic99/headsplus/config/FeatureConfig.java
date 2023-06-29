package io.github.thatsmusic99.headsplus.config;

import org.jetbrains.annotations.NotNull;

public abstract class FeatureConfig extends HPConfig {

    private boolean loaded = false;

    public FeatureConfig(@NotNull String name) throws Exception {
        super(name);
    }

    @Override
    public void load() throws Exception {
        super.load();
        setLoaded(true);
    }

    public abstract boolean shouldLoad();

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
