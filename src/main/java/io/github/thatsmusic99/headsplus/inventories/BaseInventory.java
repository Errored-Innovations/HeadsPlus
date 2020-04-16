package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.IconClickEvent;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Air;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Glass;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Stats;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.PagedLists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class BaseInventory implements InventoryHolder, Listener {

    protected static HeadsPlus hp = HeadsPlus.getInstance();
    protected static HeadsPlusMessagesManager hpc = hp.getMessagesConfig();
    protected FileConfiguration hpi;
    private Inventory inventory;
    protected PagedLists<Content> contents;
    private boolean larger;
    private UUID uuid;
    private Icon[] icons;

    // Used for config setup purposes
    public BaseInventory() {

    }

    public BaseInventory(Player player, HashMap<String, String> context) {
        hpi = hp.getItems().getConfig();
        // Decide if the inventory becomes larger
        larger = hp.getConfig().getBoolean("plugin.larger-menus");
        // Get the default icons
        icons = new Icon[hpi.getInt("inventories." + getId() + ".size")];
        uuid = player.getUniqueId();
        String items = hpi.getString("inventories." + getId() + ".icons");
        int contentsPerPage = HPUtils.matchCount(CachedValues.CONTENT_PATTERN.matcher(items));
        contents = new PagedLists<>(transformContents(context, player), contentsPerPage);
        build(context, player);
        player.openInventory(getInventory());
        hp.getServer().getPluginManager().registerEvents(this, hp);
    }

    public abstract String getDefaultTitle();

    public abstract String getDefaultItems();

    public abstract String getId();

    public void build(HashMap<String, String> context, Player player) {
        int totalPages = contents.getTotalPages();
        String currentPage = context.get("page");
        inventory = Bukkit.createInventory(this,
                hpi.getInt("inventories." + getId() + ".size"),
                hpi.getString("inventories." + getId() + ".title")
                        .replaceAll("\\{page}", currentPage)
                        .replaceAll("\\{pages}", String.valueOf(totalPages))
                        .replaceAll("\\{section}", context.get("section") != null ? context.get("section") : "None"));
        String items = hpi.getString("inventories." + getId() + ".icons");
        Iterator<Content> contentIt = contents.getContentsInPage(Integer.parseInt(currentPage)).iterator();
        for (int i = 0; i < items.length(); i++) {
            char c = items.charAt(i);
            Icon icon;
            if (InventoryManager.cachedNavIcons.containsKey(c)) {
                InventoryManager.NavIcon tempIcon = InventoryManager.cachedNavIcons.get(c);
                int currentPageInt = Integer.parseInt(currentPage);
                int resultPage = currentPageInt + tempIcon.getPagesToShift();
                if (resultPage < 1 || resultPage > contents.getTotalPages()) {
                    if ((tempIcon.getId().equalsIgnoreCase("last") && currentPageInt != contents.getTotalPages())
                            || (tempIcon.getId().equalsIgnoreCase("start") && currentPageInt != 1)) {
                        icon = tempIcon;
                        icon.initNameAndLore(icon.getId(), player);
                    } else {
                        try {
                            icon = Glass.class.getConstructor(Player.class).newInstance(player);
                        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                } else {
                    icon = tempIcon;
                    icon.initNameAndLore(icon.getId(), player);
                }
            } else {
                Class<? extends Icon> iconClass = InventoryManager.cachedIcons.get(c);
                if (iconClass != null) {
                    try {
                        if (Content.class.isAssignableFrom(iconClass)) {
                            if (contentIt.hasNext()) {
                                icon = contentIt.next();
                            } else {
                                icon = Air.class.getConstructor(Player.class).newInstance(player);
                            }

                        } else if (Stats.class.isAssignableFrom(iconClass)) {
                            icon = iconClass.getConstructor(Player.class, Integer.class).newInstance(player, totalPages);
                        } else {
                            icon = iconClass.getConstructor(Player.class).newInstance(player);
                        }
                    } catch (InvocationTargetException e) {
                        e.getTargetException().printStackTrace();
                        return;
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                        e.printStackTrace();
                        return;
                    }
                } else {
                    try {
                        icon = Air.class.getConstructor(Player.class).newInstance(player);
                        hp.getLogger().warning("Illegal icon character " + c + " has been replaced with air.");
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
        Player player = (Player) event.getWhoClicked();
        if (slot > -1 && slot < event.getInventory().getSize()) {
            event.setCancelled(true);
            IconClickEvent iconEvent = new IconClickEvent(player, icons[slot]);
            Bukkit.getPluginManager().callEvent(iconEvent);
            if (!iconEvent.isCancelled()) {
                iconEvent.setToDestroy(icons[slot].onClick((Player) event.getWhoClicked(), event));
            }
            if (iconEvent.willDestroy()) {
                destroy((Player) event.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != this) return;
        destroy((Player) event.getPlayer());
    }

    public void destroy(Player player) {
        inventory = null;
        contents = null;
        uuid = null;
        icons = null;
        InventoryManager manager = InventoryManager.getManager(player);
        HandlerList.unregisterAll(this);
        if (!manager.isGlitchSlotFilled()) {
            player.getInventory().setItem(8, new ItemStack(Material.AIR));
        }
    }

    public PagedLists<Content> getContents() {
        return contents;
    }
}
