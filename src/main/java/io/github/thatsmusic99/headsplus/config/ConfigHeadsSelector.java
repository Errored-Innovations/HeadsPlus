package io.github.thatsmusic99.headsplus.config;

public class ConfigHeadsSelector extends FeatureConfig {

    private static ConfigHeadsSelector instance;

    public ConfigHeadsSelector() {
        super("heads-selector");
        instance = this;
    }

    @Override
    public boolean shouldLoad() {
        return false;
    }

    public static ConfigHeadsSelector get() {
        return instance;
    }

    @Override
    public void loadDefaults() {
        addComment("This is where you can configure where the heads selector (/heads)");

        addDefault("autograb", false);
        addDefault("automatically-enable-grabbed-heads", true);
        addDefault("autograb-section", "players");
        addDefault("allow-favourite-heads", true, "Allow players to right click heads to add them as a favourite.");

    }
}
