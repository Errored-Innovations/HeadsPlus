package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.*;
import io.github.thatsmusic99.headsplus.inventories.icons.list.*;
import io.github.thatsmusic99.headsplus.inventories.list.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class InventoryManager {

    public enum InventoryType {
        SELLHEAD_MENU(SellheadMenu.class), // Unused right now
        SELLHEAD_CATEGORY(SellheadCategory.class),
        HEADS_MENU(HeadsMenu.class),
        HEADS_CATEGORY(HeadsSection.class),
        HEADS_SEARCH(HeadsSearch.class),
        HEADS_FAVORITES(HeadsFavourite.class),
        CHALLENGES_MENU(ChallengesMenu.class),
        CHALLENGES_LIST(ChallengesSection.class),
        CHALLENGES_PINNED(ChallengesPinnedInv.class);

        private Class<? extends BaseInventory> inventory;

        InventoryType(Class<? extends BaseInventory> inventory) {
            this.inventory = inventory;
        }

        public BaseInventory getInventory(Player player, HashMap<String, String> context) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            return inventory.getConstructor(Player.class, HashMap.class).newInstance(player, context);
        }
    }

    public enum IconType {
        CONTENT(null, 'C', null, null, null, Content.class),
        CHALLENGE("challenge", null, null, "{challenge-name}", new String[]{"{challenge-lore}",
                "{msg_inventory.icon.challenge.reward}",
                "{msg_inventory.icon.challenge.xp}",
                "{msg_inventory.icon.challenge.progress}",
                "{completed}",
                "{pinned}"}, Challenge.class),
        CHALLENGE_SECTION("challenges-section", null, null, "{section-name}",
                new String[]{"{section-lore}", "{msg_inventory.icon.challenge.count}"}, ChallengeSection.class),
        CUSTOM_HEAD("head", null, null, "{head-name}",
                new String[]{"{msg_inventory.icon.head.price}", "{msg_inventory.icon.head.favourite}"}, CustomHead.class),
        CUSTOM_HEAD_SECTION("headsection", null, null, "{head-name}", new String[]{"{msg_inventory.icon.head.count}"}, CustomHeadSection.class),
        SELLHEAD_HEAD("sellable-head", null, null, "{head-name}", new String[]{}, SellheadHead.class), // TODO
        GLASS("glass", 'G', Material.LIGHT_GRAY_STAINED_GLASS_PANE.name(), "&c", new String[]{}, Glass.class),
        CLOSE("close", 'X', Material.BARRIER.name(), "{msg_inventory.icon.close}", new String[]{}, Close.class),
        AIR("air", 'A', Material.AIR.name(), "", new String[]{}, Air.class),
        MENU("menu", 'M', Material.NETHER_STAR.name(), "{msg_inventory.icon.menu}", new String[]{}, Menu.class),
        FAVOURITES("favourites", 'F', Material.DIAMOND.name(), "{msg_inventory.icon.favourites}", new String[]{}, Favourites.class),
        STATS("stats", 'S', Material.PAPER.name(), "{msg_inventory.icon.stats.icon}", new String[]{"{msg_inventory.icon.stats.total-heads} {heads}",
                "{msg_inventory.icon.stats.total-pages} {pages}",
                "{msg_inventory.icon.stats.total-sections} {sections}",
                "{msg_inventory.icon.stats.current-balance} {balance}",
                "{msg_inventory.icon.stats.current-section} {section}"}, Stats.class),
        SEARCH("search", 'K', Material.NAME_TAG.name(), "{msg_inventory.icon.search}", new String[]{}, Search.class),
        PINNED_CHALLENGES("pinned-challenges", 'P', Material.DIAMOND.name(), "{msg_inventory.icon.pinned-challenges}", new String[]{}, ChallengesPinned.class);

        private Class<? extends Icon> icon;
        private Character c;
        private String id;
        private String material;
        private String displayName;
        private String[] lore;

        IconType(String id, Character c, String material, String name, String[] lore, Class<? extends Icon> icon) {
            this.id = id;
            this.icon = icon;
            this.material = material;
            this.displayName = name;
            this.lore = lore;
            this.c = c;
        }

        public String getId() {
            return id;
        }

        public Class<? extends Icon> getIcon() {
            return icon;
        }

        public String getMaterial() {
            return material;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String[] getLore() {
            return lore;
        }

        public Character getChar() {
            return c;
        }
    }

    public static final HashMap<UUID, InventoryManager> storedInventories = new HashMap<>(); // Stores Inventories
    public static final HashMap<Character, IconType> cachedIcons = new HashMap<>(); // Stores icons with their stored character (exception for content icons)
    public static final HashMap<Character, NavIcon> cachedNavIcons = new HashMap<>(); // Nav Icons have their own special settings

    private int currentPage; // Current page
    private BaseInventory inventory; // Inventory itself
    private boolean isGlitchSlotFilled; // dumb bug that is impossible to reproduce but i was told to fix it anyways
    private final UUID player; // Storing UUID of player to avoid memory leaks
    private InventoryType type;
    private String section;

    public static void initiateInvsAndIcons() {
        // Allow icons to have their own character
        cachedIcons.put('C', IconType.CONTENT);
        cachedIcons.put('G', IconType.GLASS);
        cachedIcons.put('X', IconType.CLOSE);
        cachedIcons.put('A', IconType.AIR);
        cachedIcons.put('M', IconType.MENU);
        cachedIcons.put('F', IconType.FAVOURITES);
        cachedIcons.put('S', IconType.STATS);
        cachedIcons.put('K', IconType.SEARCH);
        cachedIcons.put('P', IconType.PINNED_CHALLENGES);

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
        open(type, new HashMap<>());
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
            inventory = type.getInventory(Bukkit.getPlayer(player), context);
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
