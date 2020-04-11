package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.IconClickEvent;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigItems;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Air;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.PagedLists;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public abstract class BaseInventory implements InventoryHolder, Listener {

    protected static HeadsPlus hp = HeadsPlus.getInstance();
    protected static HeadsPlusMessagesManager hpc = hp.getMessagesConfig();
    protected static FileConfiguration hpi = hp.getItems().getConfig();
    private Inventory inventory;
    protected PagedLists<Content> contents;
    private boolean larger;
    private UUID uuid;
    private Icon[] icons;

    public BaseInventory(Player player, HashMap<String, String> context) {
        // Decide if the inventory becomes larger
        larger = hp.getConfig().getBoolean("plugin.larger-menus");
        // Get the default icons
        icons = new Icon[hpi.getInt("inventories." + getDefaultId() + ".size")];
        HeadsPlusConfigItems itemsConf = HeadsPlus.getInstance().getItems();
        String items = itemsConf.getConfig().getString("inventories." + getDefaultId() + ".icons");
        int contentsPerPage = HPUtils.matchCount(CachedValues.CONTENT_PATTERN.matcher(items));
        contents = new PagedLists<>(transformContents(new HashMap<>(), player), contentsPerPage);
        build(context, player);
        player.openInventory(getInventory());
        hp.getServer().getPluginManager().registerEvents(this, hp);
    }

    public abstract String getDefaultTitle();

    public abstract String getDefaultItems();

    public abstract String getDefaultId();

    public abstract String getName();

    public void build(HashMap<String, String> context, Player player) {
        int totalPages = contents.getTotalPages();
        String currentPage = context.get("page");
        inventory = Bukkit.createInventory(this,
                hpi.getInt("inventories." + getDefaultId() + ".size"),
                hpi.getString("inventories." + getDefaultId() + ".title")
                        .replaceAll("\\{page}", currentPage)
                        .replaceAll("\\{pages}", String.valueOf(totalPages)));
        String items = hpi.getString("inventories." + getDefaultId() + ".icons");
        Iterator<Content> contentIt = contents.getContentsInPage(Integer.parseInt(currentPage)).iterator();
        for (int i = 0; i < items.length(); i++) {
            char c = items.charAt(i);
            Icon icon;
            if (InventoryManager.cachedNavIcons.containsKey(c)) {
                InventoryManager.NavIcon tempIcon = InventoryManager.cachedNavIcons.get(c);
                int currentPageInt = Integer.parseInt(currentPage);
                int resultPage = currentPageInt + tempIcon.getPagesToShift();
                if (resultPage < 1 || resultPage > contents.getTotalPages()) {
                    if (tempIcon.getId().equalsIgnoreCase("last_page") || tempIcon.getId().equalsIgnoreCase("first_page")) {
                        icon = tempIcon;
                    } else {
                        try {
                            icon = Air.class.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                } else {
                    icon = tempIcon;
                }
            } else {
                Class<? extends Icon> iconClass = InventoryManager.cachedIcons.get(c);
                if (iconClass != null) {
                    try {
                        if (Content.class.isAssignableFrom(iconClass) && contentIt.hasNext()) {
                            icon = contentIt.next();
                        } else {
                            if (Content.class.isAssignableFrom(iconClass)
                                    && !contentIt.hasNext()) {
                                icon = Air.class.newInstance();
                            } else {
                                icon = iconClass.newInstance();
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                        return;
                    }
                } else {
                    try {
                        icon = Air.class.newInstance();
                        hp.getLogger().warning("Illegal icon character " + c + " has been replaced with air.");
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            inventory.setItem(i, icon.item);
            icons[i] = icon;
        }
    }

    public abstract List<Content> transformContents(HashMap<String, String> context, Player player);

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        int slot = event.getRawSlot();
        if (slot > 0 && slot < event.getInventory().getSize()) {
            event.setCancelled(true);
            IconClickEvent iconEvent = new IconClickEvent((Player) event.getWhoClicked(), icons[slot]);
            Bukkit.getPluginManager().callEvent(iconEvent);
            if (!iconEvent.isCancelled()) {
                icons[slot].onClick((Player) event.getWhoClicked(), event);
            }
            if (iconEvent.willDestroy()) {
                inventory = null;
                contents = null;
                uuid = null;
                icons = null;
            }
        }
    }

    public PagedLists<Content> getContents() {
        return contents;
    }
}
