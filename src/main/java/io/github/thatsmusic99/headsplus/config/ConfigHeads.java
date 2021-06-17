package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.CMFile;
import org.bukkit.plugin.Plugin;

public class ConfigHeads extends CMFile {

    public ConfigHeads(Plugin plugin) {
        super(plugin, "heads");
    }

    @Override
    public void loadDefaults() {
        addComment("This is the config where entirely custom heads can be made, with custom metadata, actions, etc.\n" +
                "To reference a custom head, use HP#head_id.\n" +
                "If you're looking for mobs.yml instead to change mob drops, please go there :)");

    }
}
