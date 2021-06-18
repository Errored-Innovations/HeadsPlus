package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;

public class ConfigHeads extends CMFile {

    private static ConfigHeads instance;

    public ConfigHeads() {
        super(HeadsPlus.getInstance(), "heads");
        instance = this;
    }

    @Override
    public void loadTitle() {
    }

    @Override
    public void loadDefaults() {
        addComment("This is the config where entirely custom heads can be made, with custom metadata, actions, etc.\n" +
                "To reference a custom head, use HP#head_id.\n" +
                "If you're looking for mobs.yml instead to change mob drops, please go there or use /hp config mobs :)");

        addDefault("update-heads", true, "Whether the plugin should add more heads included with updates.");


    }

    @Override
    public void moveToNew() {

    }

    public static ConfigHeads get() {
        return instance;
    }
}
