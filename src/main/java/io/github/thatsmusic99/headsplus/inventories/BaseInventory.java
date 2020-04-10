package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.IconClickEvent;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
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

    public BaseInventory(Player player) {
        this(player, null);
    }

    public BaseInventory(Player player, HashMap<String, String> context) {
        larger = hp.getConfig().getBoolean("plugin.larger-menus");
        icons = new Icon[hpi.getInt("inventories." + getDefaultId() + ".size")];
        gatherContents(context, player);
        build(context, player);
        player.openInventory(getInventory());
        hp.getServer().getPluginManager().registerEvents(this, hp);
    }
    public abstract String getDefaultTitle();

    public abstract String getDefaultItems();

    public abstract String getDefaultId();

    public abstract String getName();

    public abstract void gatherContents(HashMap<String, String> context, Player player);

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
            Class<? extends Icon> iconClass = InventoryManager.cachedIcons.get(c);
            if (iconClass != null) {
                try {
                    Icon icon = iconClass.newInstance();
                    if (icon instanceof Content) {
                        icon = contentIt.next();
                    }
                    inventory.setItem(i, icon.item);
                    icons[i] = icon;
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public abstract List<Content> transformContents(HashMap<String, String> context, Player player);

    public void refreshContents() {

    }

    public abstract String getContentType();

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != this) return;
        int slot = event.getRawSlot();
        if (slot > 0 && slot < inventory.getSize()) {
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
}
