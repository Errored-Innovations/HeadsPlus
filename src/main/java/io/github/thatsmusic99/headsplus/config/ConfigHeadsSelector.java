package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXEnums;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXSections;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import io.github.thatsmusic99.headsplus.managers.MaskManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class ConfigHeadsSelector extends FeatureConfig {

    private static ConfigHeadsSelector instance;
    private final HashMap<String, SectionInfo> sections = new LinkedHashMap<>();
    private final HashMap<String, BuyableHeadInfo> buyableHeads = new LinkedHashMap<>();
    private int totalHeads = 0;

    public ConfigHeadsSelector() throws Exception {
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
    public void addDefaults() {
        double version = getDouble("version", -1.0);
        if (isNew()) version = -1.0;
        addComment("This is where you can configure where the heads selector (/heads)");

        addDefault("version", 3.5);
        makeSectionLenient("sections");
        if (version >= 3.5) return;
        if (ConfigCustomHeads.get() != null) return;
        for (HeadsXSections section : HeadsXSections.values()) {
            if (section.version > version) {
                forceExample("sections." + section.id + ".texture", section.texture);
                forceExample("sections." + section.id + ".display-name", section.displayName);
                forceExample("sections." + section.id + ".permission", "headsplus.section." + section.id);
                forceExample("sections." + section.id + ".enabled", true);
            } else {
                addExample("sections." + section.id + ".texture", section.texture);
                addExample("sections." + section.id + ".display-name", section.displayName);
                addExample("sections." + section.id + ".permission", "headsplus.section." + section.id);
                addExample("sections." + section.id + ".enabled", true);
            }
        }

        set("version", 3.5);
    }

    @Override
    public void moveToNew() {

        // Get everything in heads
        if (getConfigSection("heads") == null) return;
        for (String key : getConfigSection("heads").getKeys(false)) {

            if (key.startsWith("HPM#")) {
                key = key.substring(4);
                moveTo("heads." + key + ".section", "heads." + key + ".section", ConfigMasks.get());
                moveTo("heads." + key + ".price", "heads." + key + ".price", ConfigMasks.get());
            } else {
                if (key.startsWith("HP#")) key = key.substring(3);
                moveTo("heads." + key + ".section", "heads." + key + ".section", ConfigHeads.get());
                moveTo("heads." + key + ".price", "heads." + key + ".price", ConfigHeads.get());
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
                    .withTexture(section.getString("texture")));
        }

        // Setting up heads
        for (String key : ConfigHeads.get().getConfigSection("heads").getKeys(false)) {
            ConfigSection section = ConfigHeads.get().getConfigSection("heads." + key);
            if (section == null) continue;

            addHead(key, section);
        }

        // Masks
        final ConfigSection masks = ConfigMasks.get().getConfigSection("masks");
        if (masks == null) return;
        for (String key : masks.getKeys(false)) {
            ConfigSection section = ConfigMasks.get().getConfigSection("masks." + key);
            if (section == null) continue;

            addHead(key, section);
        }
    }

    private void addHead(@NotNull String key, @NotNull ConfigSection section) {

        // If the section doesn't exist, continue
        if (!section.contains("section")) return;
        if (!sections.containsKey(section.getString("section"))) return;
        SectionInfo sectionInfo = sections.get(section.getString("section"));

        // Get the head info itself
        if (!HeadManager.get().contains(key)) return;
        BuyableHeadInfo headInfo = new BuyableHeadInfo(HeadManager.get().getHeadInfo(key), key);
        headInfo.withDisplayName(section.getString("display-name", null));
        headInfo.setLore(section.getList("lore", null));
        if (section.contains("price")) {
            headInfo.withPrice(section.getDouble("price", -1.0));
        }
        sectionInfo.addHead(key, headInfo);
        totalHeads++;
    }

    public HashMap<String, SectionInfo> getSections() {
        return sections;
    }

    public SectionInfo getSection(String name) {
        return sections.get(name);
    }

    public HashMap<String, BuyableHeadInfo> getBuyableHeads() {
        return buyableHeads;
    }

    public BuyableHeadInfo getBuyableHead(String id) {
        return buyableHeads.get(id);
    }

    public int getTotalHeads() {
        return totalHeads;
    }

    public static class SectionInfo {
        private String texture = null;
        private String displayName = null;
        private String permission;
        private final String id;
        private final HashMap<String, BuyableHeadInfo> heads;

        public SectionInfo(String id) {
            this.id = id;
            this.permission = "headsplus.section." + id;
            this.heads = new LinkedHashMap<>();
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

        public void addHead(String id, BuyableHeadInfo head) {
            heads.put(id, head);
            instance.getBuyableHeads().put(id, head);
        }

        public String getPermission() {
            return permission;
        }

        public String getId() {
            return id;
        }

        public HashMap<String, BuyableHeadInfo> getHeads() {
            return heads;
        }

        public ItemStack buildSection() {
            if (texture == null) throw new IllegalStateException("Texture must not be null!");
            if (!texture.startsWith("HP#") && !texture.startsWith("HPM#"))
                throw new IllegalStateException("The texture for " + id + " must be a registered (HP#) head!");
            HeadManager.HeadInfo headInfo = HeadManager.get().getHeadInfo(texture);
            HeadsPlus.debug("Building a head for " + texture + "...");
            HeadsPlus.debug("Contains texture > " + HeadManager.get().contains(texture));
            ItemStack item = headInfo.forceBuildHead();
            PersistenceManager.get().makeIcon(item);
            if (displayName == null) return item;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            item.setItemMeta(meta);
            return item;
        }
    }

    public static class BuyableHeadInfo extends MaskManager.MaskInfo {
        private double price = -1;

        public BuyableHeadInfo(HeadManager.HeadInfo info, String id) {
            super(info, id);
            this.withDisplayName(info.getDisplayName())
                    .withMaterial(info.getMaterial())
                    .withTexture(info.getTexture());
            setLore(info.getLore());
        }

        public BuyableHeadInfo withPrice(double price) {
            this.price = price;
            return this;
        }

        public double getPrice() {
            return price;
        }
    }
}
