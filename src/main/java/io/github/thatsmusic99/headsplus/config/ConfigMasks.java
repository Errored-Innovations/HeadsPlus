package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;

import java.util.Collections;

public class ConfigMasks extends CMFile {

    public ConfigMasks() {
        super(HeadsPlus.get(), "masks");
    }

    @Override
    public void loadDefaults() {
        addExample("masks.creeper.when-wearing", "animation:creeper");
        addExample("masks.creeper.effects", Collections.singletonList("INVISIBILITY"));

    }
}
