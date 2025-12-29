package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXEnums;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import org.bukkit.ChatColor;

public class ConfigHeads extends HPConfig {

    private static ConfigHeads instance;

    public ConfigHeads() throws Exception {
        super("heads.yml");
        instance = this;
    }

    @Override
    public void addDefaults() {
        addComment("This is the config where entirely custom heads can be made, with custom metadata, actions, etc.\n" +
                "To reference a custom head, use HP#head_id.\n" +
                "If you're looking for mobs.yml instead to change mob drops, please go there :)");

        addDefault("update-heads", true, "Whether the plugin should add more heads included with updates.");
        addDefault("version", 4.3);

        makeSectionLenient("heads");
        if (isNew() || getDouble("version") < 4.3) {
            if (ConfigCustomHeads.get() != null) return;
            for (HeadsXEnums head : HeadsXEnums.values()) {
                if (isNew() || head.version > getDouble("version")) {
                    forceExample("heads." + head.name().toLowerCase() + ".display-name", head.displayName);
                    forceExample("heads." + head.name().toLowerCase() + ".texture", head.texture);
                    forceExample("heads." + head.name().toLowerCase() + ".section", head.section);
                } else {
                    addExample("heads." + head.name().toLowerCase() + ".display-name", head.displayName);
                    addExample("heads." + head.name().toLowerCase() + ".texture", head.texture);
                    addExample("heads." + head.name().toLowerCase() + ".section", head.section);
                }
            }

            set("version", 4.3);
        }
    }

    @Override
    public void postSave() {
        for (String head : getConfigSection("heads").getKeys(false)) {
            ConfigSection section = getConfigSection("heads." + head);
            if (section == null) continue; // why?

            String displayName = section.getString("display-name", "");
            if (displayName == null) displayName = "";

            String texture = section.getString("texture", "");
            if (texture == null) {
                HeadsPlus.get().getLogger().warning("Head ID " + head + " has a null texture and cannot be added.");
                continue;
            }

            HeadManager.HeadInfo headInfo = new HeadManager.HeadInfo()
                    .withDisplayName(ChatColor.translateAlternateColorCodes('&', displayName))
                    .withTexture(texture);
            headInfo.setLore(section.getStringList("lore"));
            HeadManager.get().registerHead(head, headInfo);
        }

        HeadsPlus.get().getLogger().info("Registered " + HeadManager.get().getKeys().size() + " heads.");
    }

    public static ConfigHeads get() {
        return instance;
    }
}
