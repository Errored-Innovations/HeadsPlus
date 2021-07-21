package io.github.thatsmusic99.headsplus.config;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigAnimations extends HPConfig {

    public ConfigAnimations() {
        super("animations.yml");
    }

    @Override
    public void loadDefaults() {
        addComment("This is the config where you can make head animations come to life.\n" +
                "For technical reasons, these will only work in inventories and masks.");

        makeSectionLenient("animations");

        addExample("animations.creeper.looping-mode", "loop-reverse", "");
        addExample("animations.creeper.pausing-period", 4);
        addExample("animations.creeper.textures", new ArrayList<>(Arrays.asList(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vaS5pbWd1ci5jb20vQXZ1Z29aeC5wbmcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vaS5pbWd1ci5jb20vSlFLeHYzNy5wbmcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vaS5pbWd1ci5jb20vRVRKalhYVS5wbmcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vaS5pbWd1ci5jb20vdVNGTlhway5wbmcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vaS5pbWd1ci5jb20vS0ZGTjJjVy5wbmcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vaS5pbWd1ci5jb20vUUVPUWxYcy5wbmcifX19",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vaS5pbWd1ci5jb20vUlQ5TEFkYS5wbmcifX19")));
    }
}
