package io.github.thatsmusic99.headsplus.util;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.customheads.HeadInventory;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.config.customheads.icons.Nav;
import io.github.thatsmusic99.headsplus.config.customheads.inventories.*;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class InventoryManager {

    public enum Type {
        SELL, LIST_MENU, LIST_CATEGORY, LIST_SEARCH, LIST_FAVORITES, CHALLENGES_MENU, CHALLENGES_LIST
    }

    public static final HashMap<Player, InventoryManager> pls = new HashMap<>();

    private final Player player;
    private int pages;
    private String menuSection;
    private List<String> searchResults = null;
    private int headsInSection;
    private int currentPage = 0;
    private HeadInventory inventory;
    public boolean searchAnvilOpen = false;
    private Type type;
    private final HeadsPlus plugin;
    private final boolean largerMenu;
    private boolean glitchSlotFilled;
    private final HeadsPlusConfigCustomHeads hpchx;

    public int getPages() {
        return pages;
    }

    public int getPage() {
        return currentPage;
    }

    public int getHeads() {
        return headsInSection;
    }

    public String getSection() {
        return menuSection;
    }

    public Type getType() {
        return type;
    }

    public boolean isGlitchSlotFilled() {
        return glitchSlotFilled;
    }

    public void setGlitchSlotFilled(boolean glitchSlotFilled) {
        this.glitchSlotFilled = glitchSlotFilled;
    }

    public HeadInventory getInventory() {
        return inventory;
    }

    protected InventoryManager(Player p) {
        this.player = p;
        plugin = HeadsPlus.getInstance();
        hpchx = plugin.getHeadsXConfig();
        largerMenu = plugin.getConfig().getBoolean("plugin.larger-menus", false);
        glitchSlotFilled = p.getInventory().getItem(8) != null;
    }

    public static InventoryManager get(Player p) {
        return pls.get(p);
    }

    public static InventoryManager getOrCreate(Player p) {
        InventoryManager im = pls.get(p);
        if (im == null) {
            pls.put(p, im = new InventoryManager(p));
        }
        return im;
    }

    public static void inventoryClosed(Player p) {
        InventoryManager im = pls.get(p);
        if (im != null) {
            if (!im.isGlitchSlotFilled() && im.inventory != null) {
                p.getInventory().setItem(8, new ItemStack(Material.AIR));
            }
            im.inventory = null;
            im.searchAnvilOpen = false;

        }
    }

    public void showScreen(Type type) {
        this.type = type;
        currentPage = 0;
        player.closeInventory();
        if (type == Type.LIST_FAVORITES) {
            searchResults = loadFavoriteHeads();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                player.openInventory(getPageInventory());
            }
        }.runTaskLater(HeadsPlus.getInstance(), 1);

    }

    public void showSearch(String search) {
        searchResults = searchHeads(search);
        menuSection = "search:" + search;
        showScreen(Type.LIST_SEARCH);
    }

    public void showSection(String sec) {
        menuSection = sec;
        showScreen(Type.LIST_CATEGORY);
    }

    public void showChallengeSection(String sec) {
        menuSection = sec;
        showScreen(Type.CHALLENGES_LIST);
    }

    public void showPage(Nav.Page page) {
        if (page == Nav.Page.MENU) {
            switch (type) {
                case CHALLENGES_LIST:
                    // change to CHALLENGES_MENU
                    currentPage = 0;
                    type = Type.CHALLENGES_MENU;
                    break;
                case LIST_CATEGORY:
                case LIST_FAVORITES:
                case LIST_SEARCH:
                default:
                    // change to LIST_MENU
                    currentPage = 0;
                    type = Type.LIST_MENU;
                    break;
                //default:
                //    inventory = null;
                //    player.closeInventory();
                //	  return;
            }
        } else if (page == Nav.Page.START) {
            currentPage = 0;
        } else if (page == Nav.Page.LAST) {
            currentPage = pages - 1;
        } else if (page == Nav.Page.BACK) {
            currentPage = Math.max(0, currentPage - 1);
        } else if (page == Nav.Page.BACK_2) {
            currentPage = Math.max(0, currentPage - 2);
        } else if (page == Nav.Page.BACK_3) {
            currentPage = Math.max(0, currentPage - 3);
        } else if (page == Nav.Page.NEXT) {
            currentPage = Math.min(pages - 1, currentPage + 1);
        } else if (page == Nav.Page.NEXT_2) {
            currentPage = Math.min(pages - 1, currentPage + 2);
        } else if (page == Nav.Page.NEXT_3) {
            currentPage = Math.min(pages - 1, currentPage + 3);
        } else {
            return;
        }
        // change page to currentPage
        player.closeInventory();
        // somewhat legacy compatibility
        switch (type) {
            case LIST_MENU:
                menuSection = "menu";
                break;
            case LIST_FAVORITES:
                menuSection = "favourites";
                break;
            case CHALLENGES_MENU:
                menuSection = "chal";
                break;
        }
        player.openInventory(getPageInventory());
    }

    public Inventory getPageInventory() {
        switch (type) {
            case LIST_MENU:
                return getListMainMenu();
            case SELL:
                return getSellMenu();
            case LIST_SEARCH:
                return getSearchMenu();
            case LIST_FAVORITES:
                return getFavorites();
            case LIST_CATEGORY:
                return getCategory();
            case CHALLENGES_MENU:
                return getChallengeMain();
            case CHALLENGES_LIST:
                return getChallenge();
        }
        return null;
    }

    private Inventory getListMainMenu() {
        int max = charOccurance(plugin.getItems().getConfig().getString("inventories.headmenu.icons"), "L"); //4 * 7;
        boolean wide = false;
        if (largerMenu && hpchx.sections.size() > max) {
            max = (plugin.getItems().getConfig().getInt("inventories.headmenu.size") - 9);
            wide = true;
        }
        List<ItemStack> heads = new ArrayList<>();
        int start = currentPage * max;
        String[] sections = hpchx.sections.keySet().toArray(new String[0]);
        int categories = sections.length;
        if (hpchx.isAdvent()) {
            ++categories;
        }
        pages = (int) Math.max(1, Math.ceil((double) categories / max));
        headsInSection = 0;
        for (int i = start, c = 0; i < categories && c < max; ++i, ++c) {
            final int count = hpchx.sections.get(sections[i]).size();
            headsInSection += count;
            try {
                ItemStack is = hpchx.getSkull(hpchx.getConfig().getString("sections." + sections[i] + ".texture"));
                SkullMeta im = (SkullMeta) is.getItemMeta();
                final String disp = hpchx.getConfig().getString("sections." + sections[i] + ".display-name");
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', disp != null ? disp : sections[i]));
                // TODO make customisable again
                im.setLore(Collections.singletonList(ChatColor.GRAY.toString() + count + " heads"));
                is.setItemMeta(im);
                is = plugin.getNBTManager().addSection(is, sections[i]);
                heads.add(is);
            } catch (NullPointerException ex) {
                plugin.getLogger().log(Level.WARNING, "Head texture for section " + sections[i] + " not found.");
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Unexpected Error processing section " + sections[i], ex);
            }
        }
        if (hpchx.isAdvent()) {
            try {
                final String text = hpchx.getConfig().getString("options.advent-texture");
                ItemStack is;

                if (text.startsWith("HP#")) {
                    is = hpchx.getSkull(text);
                    SkullMeta sm = (SkullMeta) is.getItemMeta();
                    sm.setDisplayName(ChatColor.translateAlternateColorCodes('&', hpchx.getConfig().getString("options.advent-display-name")));
                    is.setItemMeta(sm);
                } else {
                    is = hpchx.getSkullFromTexture(text, false, hpchx.getConfig().getString("options.advent-display-name"));
                }
                is = plugin.getNBTManager().addSection(is, "advent-calendar");
                heads.add(is);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Unexpected Error processing section options.advent-texture", ex);
            }
        }
        inventory = new HeadMenu();
        return inventory.build(player, heads, "Main Menu", currentPage, pages, headsInSection, wide);
    }

    private Inventory getSellMenu() {
        HeadsPlusConfigHeads hpch = plugin.getHeadsConfig();
        int max = charOccurance(plugin.getItems().getConfig().getString("inventories.sellheadmenu.icons"), "H"); //4 * 7;
        boolean wide = false;
        if (largerMenu && hpch.mHeads.size() > max) {
            max = (plugin.getItems().getConfig().getInt("inventories.sellheadmenu.size") - 9);
            wide = true;
        }
        List<ItemStack> items = new ArrayList<>();
        for (EntityType entity : DeathEvents.heads.keySet()) {
            try {
                HashMap<String, List<ItemStack>> heads = DeathEvents.heads.get(entity);
                if (!heads.get("default").isEmpty()) {
                    ItemStack i = heads.get("default").get(0);
                    items.add(i);
                }

            } catch (Exception e) {
                DebugPrint.createReport(e, "Opening the sellhead menu", true, player);
            }
        }
        headsInSection = items.size();
        PagedLists<ItemStack> ps = new PagedLists<>(items, max);
        pages = ps.getTotalPages();

        inventory = new SellheadMenu();
        return new SellheadMenu().build(player, ps.getContentsInPage(currentPage + 1), "Sell", currentPage, pages, headsInSection, wide);
    }

    private List<String> searchHeads(String term) {
        term = term.toLowerCase();
        List<String> c = new ArrayList<>();
        for (String k : hpchx.headsCache.keySet()) {
            final String name = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', hpchx.getConfig().getString("heads." + k + ".displayname"))).toLowerCase().replaceAll("[^a-z]", "");
            if (name.contains(term)) {
                c.add(k);
            }
        }
        return c;
    }

    private List<String> loadFavoriteHeads() {
        List<String> c = new ArrayList<>();
        HPPlayer hps = HPPlayer.getHPPlayer(player);
        for (String str : hps.getFavouriteHeads()) {
            if (hpchx.headsCache.containsKey(str)) {
                c.add(str);
            }
        }
        return c;
    }

    private Inventory getSearchMenu() {
        List<ItemStack> heads = new ArrayList<>();
        int max = charOccurance(plugin.getItems().getConfig().getString("inventories.headsection.icons"), "H"); //4 * 7;
        boolean wide = false;
        if (searchResults != null) {
            if (largerMenu && searchResults.size() > max) {
                max = (plugin.getItems().getConfig().getInt("inventories.headsection.size") - 9);
                wide = true;
            }
            headsInSection = searchResults.size();
            pages = (int) Math.max(1, Math.ceil((double) headsInSection / max));
            int start = currentPage * max;
            for (int i = start, c = 0; i < searchResults.size() && c < max; ++i, ++c) {
                heads.add(skull(searchResults.get(i)));
            }
        }

        inventory = new HeadSection();
        return inventory.build(player, heads, menuSection, currentPage, pages, headsInSection, wide);
    }

    public Inventory getFavorites() {
        List<ItemStack> heads = new ArrayList<>();
        int max = charOccurance(plugin.getItems().getConfig().getString("inventories.favourites.icons"), "H"); //4 * 7;
        boolean wide = false;

        if (searchResults != null) {
            if (largerMenu && searchResults.size() > max) {
                max = (plugin.getItems().getConfig().getInt("inventories.favourites.size") - 9);
                wide = true;
            }
            headsInSection = searchResults.size();
            pages = (int) Math.max(1, Math.ceil((double) headsInSection / max));
            int start = currentPage * max;
            for (int i = start, c = 0; i < searchResults.size() && c < max; ++i, ++c) {
                heads.add(skull(searchResults.get(i)));
            }
        }

        inventory = new FavouritesMenu();
        return inventory.build(player, heads, menuSection, currentPage, pages, headsInSection, wide);
    }

    public Inventory getCategory() {
        List<String> allHeads = hpchx.sections.get(menuSection);
        List<ItemStack> heads = new ArrayList<>();
        int max = charOccurance(plugin.getItems().getConfig().getString("inventories.headsection.icons"), "H"); //4 * 7;
        boolean wide = false;

        if (allHeads != null) {
            headsInSection = allHeads.size();
            if (largerMenu && headsInSection > max) {
                max = (plugin.getItems().getConfig().getInt("inventories.headsection.size") - 9);
                wide = true;
            }
            pages = (int) Math.max(1, Math.ceil((double) headsInSection / max));
            int start = currentPage * max;
            for (int i = start, c = 0; i < headsInSection && c < max; ++i, ++c) {
                heads.add(skull(allHeads.get(i)));
            }
        } else if (menuSection.equalsIgnoreCase("advent-calendar")) {
            try {
                for (AdventCManager acm : AdventCManager.values()) {
                    if (hpchx.getConfig().getStringList("advent." + acm.name()).contains(player.getUniqueId().toString())) {
                        ItemStack is = hpchx.setTexture(acm.texture, plugin.getNMS().getSkullMaterial(1));
                        ItemMeta im = is.getItemMeta();
                        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', acm.name));
                        is.setItemMeta(im);
                        is = plugin.getNMS().setCalendarValue(is, acm.name());
                        is = plugin.getNMS().setOpen(is, true);
                        heads.add(is);
                    } else {
                        ItemStack is = hpchx.setTexture(acm.wTexture, plugin.getNMS().getSkullMaterial(1));
                        ItemMeta im = is.getItemMeta();
                        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', acm.wName));
                        is.setItemMeta(im);
                        is = plugin.getNMS().setCalendarValue(is, acm.name());
                        is = plugin.getNMS().setOpen(is, false);
                        heads.add(is);
                    }
                }
            } catch (Exception ignored) {
            }
            headsInSection = heads.size();
            if (largerMenu && headsInSection > max) {
                max = (plugin.getItems().getConfig().getInt("inventories.headsection.size") - 9);
                wide = true;
            }
            pages = (int) Math.max(1, Math.ceil((double) headsInSection / max));

            PagedLists<ItemStack> paged = new PagedLists<>(heads, max);
            heads = paged.getContentsInPage(currentPage);
        }

        inventory = new HeadSection();
        return inventory.build(player, heads, menuSection, currentPage, pages, headsInSection, wide);
    }

    public Inventory getChallengeMain() {
        List<io.github.thatsmusic99.headsplus.api.ChallengeSection> challenges = plugin.getChallengeSections();
        List<ItemStack> heads = new ArrayList<>();
        int max = charOccurance(plugin.getItems().getConfig().getString("inventories.challenges-menu.icons").split(":")[0], "S");
        max += charOccurance(plugin.getItems().getConfig().getString("inventories.challenges-menu.icons").split(":")[0], "E");
        max += charOccurance(plugin.getItems().getConfig().getString("inventories.challenges-menu.icons").split(":")[0], "R");
        max += charOccurance(plugin.getItems().getConfig().getString("inventories.challenges-menu.icons").split(":")[0], "Z");
        max += charOccurance(plugin.getItems().getConfig().getString("inventories.challenges-menu.icons").split(":")[0], "V");
        max += charOccurance(plugin.getItems().getConfig().getString("inventories.challenges-menu.icons").split(":")[0], "J");
        headsInSection = challenges.size();
        pages = (int) Math.max(1, Math.ceil((double) headsInSection / max));
        for (int i = currentPage * max, c = 0; i < headsInSection && c < max; ++i, ++c) {
            io.github.thatsmusic99.headsplus.api.ChallengeSection ch = challenges.get(i);
            ItemStack is = new ItemStack(ch.getMaterial(), 1, ch.getMaterialData());
            is = plugin.getNBTManager().setChallengeSection(is, ch);
            heads.add(is);
        }
        inventory = new ChallengesMenu();
        return inventory.build(player, heads, "Challenges", currentPage, pages, headsInSection, false);
    }

    public Inventory getChallenge() {
        // menuSection
        io.github.thatsmusic99.headsplus.api.ChallengeSection section = null;
        for (io.github.thatsmusic99.headsplus.api.ChallengeSection section2 : plugin.getChallengeSections()) {
            if (section2.getName().equalsIgnoreCase(menuSection)) {
                section = section2;
                break;
            }
        }
        List<Challenge> challenges = section.getChallenges();

        List<ItemStack> heads = new ArrayList<>();
        int max = charOccurance(plugin.getItems().getConfig().getString("inventories.challenge-section.icons"), "C");
        headsInSection = challenges.size();
        pages = (int) Math.max(1, Math.ceil((double) headsInSection / max));
        for (int i = currentPage * max, c = 0; i < headsInSection && c < max; ++i, ++c) {
            Challenge ch = challenges.get(i);
            ItemStack is;
            if (ch.isComplete(player)) {
                is = ch.getCompleteIcon();
            } else {
                is = ch.getIcon();
            }
            is = plugin.getNBTManager().setChallenge(is, ch);
            heads.add(is);
        }
        inventory = new ChallengeSection();
        return inventory.build(player, heads, menuSection, currentPage, pages, headsInSection, false);
    }

    private ItemStack skull(String str) {
        ItemStack s = hpchx.getSkull(str);
        List<String> price = new ArrayList<>();
        double pr = 0.0;
        if (plugin.econ()) {
            if (hpchx.getConfig().get("options.price-per-world." + player.getWorld().getName()) instanceof String) {
                if (((String) hpchx.getConfig().get("options.price-per-world." + player.getWorld().getName())).equalsIgnoreCase("default")) {
                    if (!hpchx.getConfig().get("options.default-price").equals("free")) {
                        pr = hpchx.getConfig().getDouble("options.default-price");
                    }
                } else {
                    pr = hpchx.getConfig().getDouble("options.price-per-world." + player.getWorld().getName());
                }
            } else if (hpchx.getConfig().get("options.price-per-world." + player.getWorld().getName()) instanceof Double) {
                pr = hpchx.getConfig().getDouble("options.price-per-world." + player.getWorld().getName());
            } else if (hpchx.getConfig().get("heads." + str + ".price") instanceof String) {
                if (!((String) hpchx.getConfig().get("heads." + str + ".price")).equalsIgnoreCase("free")) {
                    if (((String) hpchx.getConfig().get("heads." + str + ".price")).equalsIgnoreCase("default")) {
                        if (!hpchx.getConfig().get("options.default-price").equals("free")) {
                            pr = hpchx.getConfig().getDouble("options.default-price");
                        }
                    } else {
                        pr = hpchx.getConfig().getDouble("heads." + str + ".price");
                    }
                }
            } else {
                if (!((hpchx.getConfig().getDouble("heads." + str + ".price")) == 0.0)) {
                    pr = hpchx.getConfig().getDouble("heads." + str + ".price");
                }
            }
        }
        price.add(ChatColor.translateAlternateColorCodes('&', ChatColor.GOLD + "[" + ChatColor.YELLOW + "Price" + ChatColor.GOLD + "] " + ChatColor.GREEN + pr));

        HPPlayer hps = HPPlayer.getHPPlayer(player);
        if (hps.hasHeadFavourited(str)) {
            price.add(ChatColor.GOLD + "Favourite!");
        }
        ItemMeta sm = s.getItemMeta();
        sm.setLore(price);
        s.setItemMeta(sm);
        s = plugin.getNBTManager().addDatabaseHead(s, str, pr);
        return s;
    }

    private int charOccurance(String s, String c) {
        int count = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (String.valueOf(s.charAt(i)).equalsIgnoreCase(c)) {
                ++count;
            }
        }
        return count;
    }
}
