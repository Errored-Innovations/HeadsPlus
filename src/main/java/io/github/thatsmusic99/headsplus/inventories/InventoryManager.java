package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.list.*;
import io.github.thatsmusic99.headsplus.inventories.list.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {

    public enum InventoryType {
        SELLHEAD_MENU, // Unused right now
        SELLHEAD_CATEGORY,
        HEADS_MENU,
        HEADS_CATEGORY,
        HEADS_SEARCH,
        HEADS_FAVORITES,
        CHALLENGES_MENU,
        CHALLENGES_LIST,
        CHALLENGES_PINNED
    }

    public static final HashMap<UUID, InventoryManager> storedInventories = new HashMap<>(); // Stores Inventories
    public static final HashMap<InventoryType, Class<? extends BaseInventory>> inventories = new HashMap<>(); // Allows inventories to be retrieved via type
    public static final HashMap<Character, Class<? extends Icon>> cachedIcons = new HashMap<>(); // Stores icons with their stored character (exception for content icons)
    public static final HashMap<Character, NavIcon> cachedNavIcons = new HashMap<>(); // Nav Icons have their own special settings

    private int currentPage; // Current page
    private BaseInventory inventory; // Inventory itself
    private boolean isGlitchSlotFilled; // dumb bug that is impossible to reproduce but i was told to fix it anyways
    private final UUID player; // Storing UUID of player to avoid memory leaks
    private InventoryType type;
    private String section;

    public static void initiateInvsAndIcons() {
        // Initiate inventories
        inventories.put(InventoryType.HEADS_MENU, HeadsMenu.class);
        inventories.put(InventoryType.HEADS_CATEGORY, HeadsSection.class);
        inventories.put(InventoryType.HEADS_FAVORITES, HeadsFavourite.class);
        inventories.put(InventoryType.HEADS_SEARCH, HeadsSearch.class);
        inventories.put(InventoryType.CHALLENGES_MENU, ChallengesMenu.class);
        inventories.put(InventoryType.CHALLENGES_LIST, ChallengesSection.class);
        inventories.put(InventoryType.CHALLENGES_PINNED, ChallengesPinnedInv.class);
        inventories.put(InventoryType.SELLHEAD_CATEGORY, SellheadCategory.class);

        // Allow icons to have their own character
        cachedIcons.put('C', Content.class);
        cachedIcons.put('G', Glass.class); // Ahaha, glass and class!! Haha, get it? Ahahahaaahhh...
        cachedIcons.put('X', Close.class); // OH MY GOD CLOSE AND CLASS OH MY FU- wait, that doesn't have the same ring to it. :(
        cachedIcons.put('A', Air.class);
        cachedIcons.put('M', Menu.class);
        cachedIcons.put('F', Favourites.class);
        cachedIcons.put('S', Stats.class);
        cachedIcons.put('K', Search.class);
        cachedIcons.put('P', ChallengesPinned.class);

        initNavIcons();
    }

    public InventoryManager(@NotNull Player player) {
        this.player = player.getUniqueId();
        currentPage = 1;
        storedInventories.put(player.getUniqueId(), this);
        isGlitchSlotFilled = player.getInventory().getItem(8) != null;
    }

    public static InventoryManager getManager(@NotNull Player player) {
        InventoryManager manager = storedInventories.get(player.getUniqueId());
        return manager == null ? new InventoryManager(player) : manager;
    }

    public void movePage(int pages) {
        currentPage += pages;
        if (inventory == null) return;
        if (currentPage < 1) {
            currentPage = 1;
        } else if (currentPage > inventory.contents.getTotalPages()) {
            currentPage = inventory.contents.getTotalPages();
        }
        open(type, inventory.context);
    }

    public void setGlitchSlot(boolean flag) {
        isGlitchSlotFilled = flag;
    }

    public boolean isGlitchSlotFilled() {
        return isGlitchSlotFilled;
    }

    public void open(InventoryType type, HashMap<String, String> context) {
        // Check if a new inventory is being opened and reset the page
        if (type == null) {
            Bukkit.getPlayer(player).closeInventory();
            return;
        }
        if (type != this.type) {
            currentPage = 1;
            section = null;
        }
        context.put("page", String.valueOf(currentPage));
        if (context.containsKey("section")) {
            section = context.get("section");
        } else {
            context.put("section", section);
        }
        try {
            inventory = inventories.get(type).getConstructor(Player.class, HashMap.class).newInstance(Bukkit.getPlayer(player), context);
            this.type = type;
        } catch (InvocationTargetException ex) {
            ex.getTargetException().printStackTrace(); // Invoked constructor threw an exception
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public InventoryType getType() {
        return type;
    }

    public static void initNavIcons() {
        // TODO: Try and actually send to the first/last page rather than using hardcoded numbers
        cachedNavIcons.put('<', new NavIcon(-999, "start"));
        cachedNavIcons.put('>', new NavIcon(999, "last"));
        cachedNavIcons.put('{', new NavIcon(-3, "back_3"));
        cachedNavIcons.put('}', new NavIcon(3, "next_3"));
        cachedNavIcons.put('[', new NavIcon(-2, "back_2"));
        cachedNavIcons.put(']', new NavIcon(2, "next_2"));
        cachedNavIcons.put('B', new NavIcon(-1, "back"));
        cachedNavIcons.put('N', new NavIcon(1, "next"));
    }

    public String getSection() {
        return section;
    }

    public static class NavIcon extends Icon {

        private final int pagesToShift;
        private final String id;

        @Override
        public boolean onClick(Player player, InventoryClickEvent event) {
            initNameAndLore(id, player);
            InventoryManager manager = InventoryManager.getManager(player);
            manager.movePage(pagesToShift);
            return true;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getDefaultMaterial() {
            return "ARROW";
        }

        @Override
        public int getDefaultDataValue() {
            return 0;
        }

        @Override
        public String getDefaultDisplayName() {
            return "{msg_inventory.icon." + id.replaceAll("_", "-") + "}";
        }

        @Override
        public String[] getDefaultLore() {
            return new String[0];
        }

        public NavIcon(int shiftPages, String type) {
            super(type);
            this.id = type;
            pagesToShift = shiftPages;

        }

        public int getPagesToShift() {
            return pagesToShift;
        }
    }

    public BaseInventory getInventory() {
        return inventory;
    }
}
