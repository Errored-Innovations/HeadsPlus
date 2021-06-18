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
        addComment("This is where you can configure where the heads selector (/heads)");

        addDefault("autograb", false);
        addDefault("automatically-enable-grabbed-heads", true);
        addDefault("autograb-section", "players");
        addDefault("allow-favourite-heads", true, "Allow players to right click heads to add them as a favourite.");


    }
}
