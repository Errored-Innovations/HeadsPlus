package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.config.ConfigCrafting;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class CraftingManager {

    private HashMap<NamespacedKey, Recipe> recipes;
    private static CraftingManager instance;

    public CraftingManager() {
        instance = this;
        recipes = new LinkedHashMap<>();
    }

    public void init() {
        clear();
        ConfigCrafting crafting = ConfigCrafting.get();
    }

    public void clear() {
        for (NamespacedKey key : recipes.keySet()) {
            Bukkit.removeRecipe(key);
        }
    }

    public void addRecipe(String key, ConfigurationSection section) {
        // Get the result section
        ConfigurationSection resultSection = section.getConfigurationSection("result");
        HPUtils.notNull(resultSection, "Recipe " + key + " does not have a result section!");

    }

    public static CraftingManager get() {
        return instance;
    }
}
