package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.list.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        }
        for (String s : Arrays.asList("next", "next_2", "next_3", "back", "back_2", "back_3", "start", "last")) {
            addDefault("icons." + s + ".material", "ARROW");
            addDefault("icons." + s + ".data-value", 0);
            addDefault("icons." + s + ".display-name", "{msg_inventory.icon." + s.replaceAll("_", "-") + "}");
            addDefault("icons." + s + ".lore", new ArrayList<>());
        }
        for (BaseInventory inv : Arrays.asList(new ChallengesMenu(),
                new ChallengesSection(),
                new HeadsFavourite(),
                new HeadsMenu(),
                new HeadsSection(),
                new SellheadCategory(),
                new SellheadMenu(), new ChallengesPinnedInv())) {
            addDefault("inventories." + inv.getId() + ".title", inv.getDefaultTitle());
            if (get("inventories." + inv.getId() + ".icons") instanceof List) {
                HeadsPlus.get().getLogger().warning("Old format for inventories.yml detected for " + inv.getId() + "! Starting over...");
                set("inventories." + inv.getId() + ".icons", inv.getDefaultItems());
            }
            addDefault("inventories." + inv.getId() + ".icons", inv.getDefaultItems());
            addDefault("inventories." + inv.getId() + ".size", 54);
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
                    set("inventories." + inventory + ".icons", items.replaceAll("[HL]", "C"));
                }
            }
        }
        addDefault("version", 0.1);
    }
}
