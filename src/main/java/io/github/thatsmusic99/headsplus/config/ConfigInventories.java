package io.github.thatsmusic99.headsplus.config;

<<<<<<< HEAD:src/main/java/io/github/thatsmusic99/headsplus/config/HeadsPlusConfigItems.java
=======
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
>>>>>>> configuration-rewrite:src/main/java/io/github/thatsmusic99/headsplus/config/ConfigInventories.java
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.list.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

<<<<<<< HEAD:src/main/java/io/github/thatsmusic99/headsplus/config/HeadsPlusConfigItems.java
public class HeadsPlusConfigItems extends ConfigSettings {

    public HeadsPlusConfigItems() {
        conName = "inventories";
        enable();
    }

    @Override
    protected void load() {
        for (Icon i : Arrays.asList(new Challenge(),
                new ChallengeSection(), new CustomHead(),
                new CustomHeadSection(), new SellheadHead(), new Air(),
                new ChallengesPinned(), new Close(), new Favourites(),
                new Glass(), new Menu(), new Search(), new Stats())) {
            getConfig().addDefault("icons." + i.getId() + ".material", i.getDefaultMaterial());
            getConfig().addDefault("icons." + i.getId() + ".data-value", i.getDefaultDataValue());
            getConfig().addDefault("icons." + i.getId() + ".display-name", i.getDefaultDisplayName());
            getConfig().addDefault("icons." + i.getId() + ".lore", i.getDefaultLore());
=======
public class ConfigInventories extends HPConfig {

    private static ConfigInventories instance;

    public ConfigInventories() {
        super("inventories.yml");
        instance = this;
    }

    public static ConfigInventories get() {
        return instance;
    }

    @Override
    public void loadDefaults() {
        for (InventoryManager.IconType i : InventoryManager.IconType.values()) {
            addDefault("icons." + i.getId() + ".material", i.getMaterial());
            addDefault("icons." + i.getId() + ".display-name", i.getDisplayName());
            addDefault("icons." + i.getId() + ".lore", i.getLore());
>>>>>>> configuration-rewrite:src/main/java/io/github/thatsmusic99/headsplus/config/ConfigInventories.java
        }
        for (String s : Arrays.asList("next", "next_2", "next_3", "back", "back_2", "back_3", "start", "last")) {
            getConfig().addDefault("icons." + s + ".material", "ARROW");
            getConfig().addDefault("icons." + s + ".data-value", 0);
            getConfig().addDefault("icons." + s + ".display-name", "{msg_inventory.icon." + s.replaceAll("_", "-") + "}");
            getConfig().addDefault("icons." + s + ".lore", new ArrayList<>());
        }
        for (BaseInventory inv : Arrays.asList(new ChallengesMenu(),
                new ChallengesSection(),
                new HeadsFavourite(),
                new HeadsMenu(),
                new HeadsSection(),
                new SellheadCategory(),
                new SellheadMenu(), new ChallengesPinnedInv())) {
<<<<<<< HEAD:src/main/java/io/github/thatsmusic99/headsplus/config/HeadsPlusConfigItems.java
            getConfig().addDefault("inventories." + inv.getId() + ".title", inv.getDefaultTitle());
            if (getConfig().get("inventories." + inv.getId() + ".icons") instanceof List) {
                HeadsPlus.getInstance().getLogger().warning("Old format for inventories.yml detected for " + inv.getId() + "! Starting over...");
                getConfig().set("inventories." + inv.getId() + ".icons", inv.getDefaultItems());
=======
            addDefault("inventories." + inv.getId() + ".title", inv.getDefaultTitle());
            if (get("inventories." + inv.getId() + ".icons") instanceof List) {
                HeadsPlus.get().getLogger().warning("Old format for inventories.yml detected for " + inv.getId() + "! Starting over...");
                set("inventories." + inv.getId() + ".icons", inv.getDefaultItems());
>>>>>>> configuration-rewrite:src/main/java/io/github/thatsmusic99/headsplus/config/ConfigInventories.java
            }
            getConfig().addDefault("inventories." + inv.getId() + ".icons", inv.getDefaultItems());
            getConfig().addDefault("inventories." + inv.getId() + ".size", 54);
        }
        if (getDouble("version") < 0.1) {
            ConfigSection section = getConfigSection("inventories");
            if (section != null) {
                for (String inventory : section.getKeys(false)) {
                    String items = getString("inventories." + inventory + ".icons");
                    if (inventory.equalsIgnoreCase("challenges-menu")) {
                        items = items.replaceAll("[S]", "C");
                        char[] chars = items.toCharArray();
                        chars[0] = 'P';
                        set("inventories." + inventory + ".icons", String.valueOf(chars));
                        continue;
                    } else if (inventory.equalsIgnoreCase("challenge-section")) {
                        char[] chars = items.toCharArray();
                        chars[0] = 'P';
                        set("inventories." + inventory + ".icons", String.valueOf(chars));
                        continue;
                    }
                    getConfig().set("inventories." + inventory + ".icons", items.replaceAll("[HL]", "C"));
                }
            }
        }
        getConfig().addDefault("version", 0.1);
        getConfig().options().copyDefaults(true);
        save();
    }
}
