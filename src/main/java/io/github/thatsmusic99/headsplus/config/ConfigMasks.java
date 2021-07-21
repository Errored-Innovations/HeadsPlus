package io.github.thatsmusic99.headsplus.config;

import java.util.Collections;

public class ConfigMasks extends FeatureConfig {

    public ConfigMasks() {
        super("masks.yml");
    }

    @Override
    public void loadDefaults() {
        addExample("masks.creeper.when-wearing", "animation:creeper");
        addExample("masks.creeper.effects", Collections.singletonList("INVISIBILITY"));

    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().MASKS;
    }
}
