package io.github.thatsmusic99.headsplus.inventories;

import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {

    public enum Type {
        SELLHEAD_MENU,
        SELLHEAD_CATEGORY,
        HEADS_MENU,
        HEADS_CATEGORY,
        HEADS_SEARCH,
        HEADS_FAVORITES,
        CHALLENGES_MENU,
        CHALLENGES_LIST
    }

    public static final HashMap<UUID, InventoryManager> storedInventories = new HashMap<>(); // Stores Inventories
    public static final HashMap<String, String> cachedValues = new HashMap<>(); // Stores placeholders such as head count
}
