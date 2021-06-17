package io.github.thatsmusic99.headsplus.managers;

import org.bukkit.inventory.Recipe;

import java.util.HashMap;

public class CraftingManager {

    private HashMap<String, Recipe> recipes;
    private static CraftingManager instance;

    public CraftingManager() {
        instance = this;
    }
}
