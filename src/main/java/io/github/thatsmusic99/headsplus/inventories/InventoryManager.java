package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Air;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Close;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Glass;
import io.github.thatsmusic99.headsplus.inventories.list.HeadsMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {

    public enum InventoryType {
        SELLHEAD_MENU,
        SELLHEAD_CATEGORY, // Unused right now
        HEADS_MENU,
        HEADS_CATEGORY,
        HEADS_SEARCH,
        HEADS_FAVORITES,
        CHALLENGES_MENU,
        CHALLENGES_LIST,
        CHALLENGES_PINNED
    }

    public static final HashMap<UUID, InventoryManager> storedInventories = new HashMap<>(); // Stores Inventories
    public static final HashMap<String, String> cachedValues = new HashMap<>(); // Stores placeholders such as head count
    public static final HashMap<InventoryType, Class<? extends BaseInventory>> inventories = new HashMap<>(); // Allows inventories to be retrieved via type
    public static final HashMap<Character, Class<? extends Icon>> cachedIcons = new HashMap<>(); // Stores icons with their stored character (exception for content icons)

    private int currentPage; // Current page
    private BaseInventory inventory; // Inventory itself
    private boolean isGlitchSlotFilled; // dumb bug that is impossible to reproduce but i was told to fix it anyways
    private UUID player; // Storing UUID of player to avoid memory leaks
    private InventoryType type;

    public static void initiateInvsAndIcons() {
        inventories.put(InventoryType.HEADS_MENU, HeadsMenu.class);

        // Allow icons to have their own
        cachedIcons.put('C', Content.class);
        cachedIcons.put('G', Glass.class); // Ahaha, glass and class!! Haha, get it? Ahahahaaahhh...
        cachedIcons.put('X', Close.class); // OH MY GOD CLOSE AND CLASS OH MY FU- wait, that doesn't have the same ring to it. :(
        cachedIcons.put('A', Air.class);
    }

    public InventoryManager(@NotNull Player player) {
        this.player = player.getUniqueId();
        currentPage = 1;
        storedInventories.put(player.getUniqueId(), this);
    }

    public static InventoryManager getManager(@NotNull Player player) {
        InventoryManager manager = storedInventories.get(player.getUniqueId());
        return manager == null ? new InventoryManager(player) : manager;
    }

    public void setGlitchSlot(boolean flag) {
        isGlitchSlotFilled = flag;
    }

    public void isGlitchSlotFilled(boolean flag) {
        isGlitchSlotFilled = flag;
    }

    public void open(InventoryType type, HashMap<String, String> context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        context.put("page", String.valueOf(currentPage));
        inventory = inventories.get(type).getConstructor(Player.class, HashMap.class).newInstance(Bukkit.getPlayer(player), context);
        this.type = type;
    }



}
