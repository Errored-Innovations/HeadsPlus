package io.github.thatsmusic99.headsplus.config;

import com.google.common.collect.Lists;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.config.defaults.CraftingDefaults;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigCrafting extends FeatureConfig {

    private static ConfigCrafting instance;

    public ConfigCrafting() throws IOException {
        super("crafting.yml");
        instance = this;
    }

    public static ConfigCrafting get() {
        return instance;
    }

    @Override
    public void loadDefaults() {
        addDefault("defaults.price", 0.0);
        addExample("defaults.lore", Lists.newArrayList("&7Price &8» &c{price}"));
        addDefault("defaults.sellable", false);

        makeSectionLenient("recipes");
        for (CraftingDefaults defaultOption : CraftingDefaults.values()) {
            String key = defaultOption.name().toLowerCase();
            addExample("recipes." + key + ".recipe-type", defaultOption.getRecipeType().name());
            addExample("recipes." + key + ".ingredients", defaultOption.getMaterials());
            addExample("recipes." + key + ".result.head", defaultOption.getHead());
        }
    }

    @Override
    public void moveToNew() {
        // If there's only two keys, you probably shouldn't care unless some asshole decides 1 recipe is acceptable
        if (existingValues.size() == 2) return;
        boolean usingBaseItem = true;
        String baseItemMaterial = "SKELETON_SKULL";

        ConfigSection baseItem = getConfigSection("base-item");
        if (baseItem != null) {
            usingBaseItem = baseItem.getBoolean("use-base-item", true);
            baseItemMaterial = baseItem.getString("material");
        }

        for (String str : existingValues.keySet()) {
            if (!(existingValues.get(str) instanceof ConfigSection)) continue;
            ConfigSection section = (ConfigSection) existingValues.get(str);
            if (str.equals("recipes")) {
                if (section.getKeys(false).size() == 0) continue;
                if (section.get(section.getKeys(false).get(0)) instanceof ConfigSection) return;
            }
            if (str.equals("base-item")) {
                moveTo("base-item.lore", "defaults.lore");
                moveTo("base-item.price", "defaults.price");
            } else {
                boolean shaped = section.getBoolean("shaped");
                set("recipes." + str + ".recipe-type", shaped ? "SHAPED" : "SHAPELESS");
                moveTo(str + ".head", "recipes." + str + ".result.head");
                for (String option : Arrays.asList("price", "display-name", "lore")) {
                    if (section.get(option) == null) continue;
                    if (!section.get(option).equals("{default}")) {
                        moveTo(str + "." + option, "recipes." + str + "." + option);
                    }
                }

                if (!shaped && usingBaseItem) {
                    List<String> ingredients = section.getList("ingredients");
                    ingredients.add(baseItemMaterial);
                    set("recipes." + str + ".ingredients", ingredients);
                } else {
                    moveTo(str + ".ingredients", "recipes." + str + ".ingredients");
                }
            }

            set(str, null);
        }
    }

    public List<String> getLore(String key) {
        List<String> lore = new ArrayList<>();
        try {
            if (get(key + ".lore").equals("{default}")) {
                for (String str : getStringList("base-item.lore")) {
                    lore.add(MessagesManager.get().formatMsg(str, null)
                            .replaceAll("\\{type}", getString(key + ".display-type"))
                            .replaceAll("\\{price}", String.valueOf(getPrice(key))));
                }
            } else {
                for (String str : getStringList(key + ".lore")) {
                    lore.add(MessagesManager.get().formatMsg(str, null));
                }
            }
        } catch (NullPointerException ex) {
            for (String str : getStringList(key + ".lore")) {
                lore.add(MessagesManager.get().formatMsg(str, null));
            }
        }

        return lore;
    }

    public double getPrice(String key) {
        return getDouble(key + ".price", getDouble("base-item.price"));
    }

    public String getDisplayName(String key) {
        return MessagesManager.get().formatMsg(getString(key + ".display-name", getString("base-item.display-name")), null)
                .replaceAll("\\{type}", getString(key + ".display-type", getString("base-item.display-type")));
    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().ENABLE_CRAFTING;
    }
}
