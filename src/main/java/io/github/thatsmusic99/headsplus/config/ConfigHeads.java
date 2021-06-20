package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXEnums;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import org.bukkit.configuration.ConfigurationSection;

public class ConfigHeads extends CMFile {

    private static ConfigHeads instance;

    public ConfigHeads() {
        super(HeadsPlus.get(), "heads");
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
        addDefault("version", 3.5);

        addLenientSection("heads");
        if (isNew() || getDouble("version") < 3.5) {
            for (HeadsXEnums head : HeadsXEnums.values()) {
                if (head.version > getDouble("version")) {
                    addDefault("heads." + head.name() + ".display-name", head.displayName);
                    addDefault("heads." + head.name() + ".texture", head.texture);
                }
            }
        }

    }

    @Override
    public void postSave() {
        for (String head : getConfig().getConfigurationSection("heads").getKeys(false)) {
            ConfigurationSection section = getConfig().getConfigurationSection("heads." + head);
            if (section == null) continue; // why?
            HeadManager.HeadInfo headInfo = new HeadManager.HeadInfo()
                    .withDisplayName(section.getString("display-name", ""))
                    .withTexture(section.getString("texture", ""));
            headInfo.setLore(section.getStringList("lore"));
            HeadManager.get().registerHead(head, headInfo);
        }
    }

    @Override
    public void moveToNew() {

    }

    public static ConfigHeads get() {
        return instance;
    }
}
