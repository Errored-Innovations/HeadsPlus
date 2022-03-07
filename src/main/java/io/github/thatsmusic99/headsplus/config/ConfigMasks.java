package io.github.thatsmusic99.headsplus.config;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.headsplus.managers.MaskManager;

import java.io.IOException;
import java.util.Collections;

public class ConfigMasks extends FeatureConfig {

    private static ConfigMasks instance;

    public ConfigMasks() throws IOException {
        super("masks.yml");
        instance = this;
    }

    @Override
    public void loadDefaults() {
        makeSectionLenient("masks");
        addExample("masks.chicken.when-wearing", new String[0]);
        addExample("masks.chicken.effects", Collections.singletonList("SLOW_FALLING"));
        addExample("masks.chicken.type", "potion");
        addExample("masks.chicken.idle", "HP#chicken");

        addExample("masks.iron_golem.when-wearing", new String[0]);
        addExample("masks.iron_golem.effects", Lists.newArrayList("INCREASE_DAMAGE:2"));
        addExample("masks.iron_golem.type", "potion");
        addExample("masks.iron_golem.idle", "HP#iron_golem");

        addExample("masks.ocelot.when-wearing", new String[0]);
        addExample("masks.ocelot.effects", Collections.singletonList("SPEED"));
        addExample("masks.ocelot.type", "potion");
        addExample("masks.ocelot.idle", "HP#ocelot");
    }

    public static ConfigMasks get() {
        return instance;
    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().MASKS;
    }

    @Override
    public void postSave() {
        new MaskManager();
    }
}
