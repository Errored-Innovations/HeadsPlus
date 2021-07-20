package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXEnums;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXSections;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConfigHeadsSelector extends FeatureConfig {

    private static ConfigHeadsSelector instance;
    private HashMap<String, SectionInfo> sections = new LinkedHashMap<>();
    private int totalHeads = 0;

    public ConfigHeadsSelector() {
        super("heads-selector.yml");
        instance = this;
    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().HEADS_SELECTOR;
    }

    public static ConfigHeadsSelector get() {
        return instance;
    }

    @Override
    public void loadDefaults() {
        double version = getDouble("version", 0.0);
        if (isNew()) version = 0.0;
        addComment("This is where you can configure where the heads selector (/heads)");

        addDefault("version", 3.5);
        makeSectionLenient("sections");
        makeSectionLenient("heads");
        if (version >= 3.5) return;
        for (HeadsXSections section : HeadsXSections.values()) {
            if (section.version > version) {
                addDefault("sections." + section.id + ".texture", section.texture);
                addDefault("sections." + section.id + ".display-name", section.displayName);
                addDefault("sections." + section.id + ".permission", "headsplus.section." + section.id);
                addDefault("sections." + section.id + ".enabled", true);
            }
        }
        for (HeadsXEnums head : HeadsXEnums.values()) {
            if (head.version > version) {
                addDefault("heads.HP#" + head.name().toLowerCase() + ".section", head.section);
            }
        }
    }

    @Override
    public void postSave() {
        sections.clear();
        totalHeads = 0;
        // Setting up sections
        for (String key : getConfigSection("sections").getKeys(false)) {
            ConfigSection section = getConfigSection("sections." + key);
            if (section == null) continue;
            // If the section isn't enabled, continue
            if (!section.getBoolean("enabled")) continue;
            sections.put(key, new SectionInfo(key)
                    .withDisplayName(section.getString("display-name", null))
                    .withPermission(section.getString("permission"))
                    .withTexture("texture"));
        }
        // Setting up heads
        for (String key : getConfigSection("heads").getKeys(false)) {
            ConfigSection section = getConfigSection("heads." + key);
            if (section == null) continue;
            // If the section doesn't exist, continue
            if (!section.contains("section")) continue;
            if (!sections.containsKey(section.getString("section"))) continue;
            SectionInfo sectionInfo = sections.get(section.getString("section"));
            // Get the head info itself
            if (!HeadManager.get().contains(key)) continue;
            // TODO - lore
            HeadManager.HeadInfo headInfo = HeadManager.get().getHeadInfo(key)
                    .withDisplayName(section.getString("display-name", null));
            sectionInfo.addHead(headInfo);
            totalHeads++;
        }
    }

    public HashMap<String, SectionInfo> getSections() {
        return sections;
    }

    public int getTotalHeads() {
        return totalHeads;
    }

    public static class SectionInfo {
        private String texture = null;
        private String displayName = null;
        private String permission;
        private final String id;
        private final List<HeadManager.HeadInfo> heads;

        public SectionInfo(String id) {
            this.id = id;
            this.permission = "headsplus.section." + id;
            this.heads = new ArrayList<>();
        }

        public SectionInfo withPermission(String permission) {
            this.permission = permission;
            return this;
        }

        public SectionInfo withTexture(String texture) {
            this.texture = texture;
            return this;
        }

        public SectionInfo withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public void addHead(HeadManager.HeadInfo head) {
            heads.add(head);
        }

        public String getPermission() {
            return permission;
        }

        public String getId() {
            return id;
        }

        public List<HeadManager.HeadInfo> getHeads() {
            return heads;
        }

        public CompletableFuture<ItemStack> buildSection() {
            if (texture == null) throw new IllegalStateException("Texture must not be null!");
            if (!texture.startsWith("HP#")) throw new IllegalStateException("The texture must be a registered (HP#) head!");
            HeadManager.HeadInfo headInfo = HeadManager.get().getHeadInfo(texture);
            return headInfo.buildHead().thenApply(item -> {
                PersistenceManager.get().makeIcon(item);
                if (displayName == null) return item;
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return item;
                meta.setDisplayName(displayName);
                item.setItemMeta(meta);
                return item;
            });
        }
    }

    public static class BuyableHeadInfo extends HeadManager.HeadInfo {
        private double price;

        public BuyableHeadInfo(HeadManager.HeadInfo info) {
            this.withDisplayName(info.getDisplayName())
                    .withMaterial(info.getMaterial())
                    .withTexture(info.getTexture());
            setLore(info.getLore());
        }
    }
}
