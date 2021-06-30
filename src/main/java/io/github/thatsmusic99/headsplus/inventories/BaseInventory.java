package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.IconClickEvent;
import io.github.thatsmusic99.headsplus.config.ConfigInventories;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Air;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Glass;
import io.github.thatsmusic99.headsplus.inventories.icons.list.Stats;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.PagedLists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class BaseInventory implements InventoryHolder, Listener {

    protected static HeadsPlus hp = HeadsPlus.get();
    protected static HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();
    private static final Pattern PAGE = Pattern.compile("\\{page}");
    private static final Pattern PAGES = Pattern.compile("\\{pages}");
    private static final Pattern SECTION = Pattern.compile("\\{section}");
    protected ConfigFile hpi;
    private Inventory inventory;
    protected PagedLists<Content> contents;
    private UUID uuid;
    private Icon[] icons;
    protected boolean suppressWarnings;
    private boolean destroy = false;

    // Used for config setup purposes
    public BaseInventory() {

    }

    public BaseInventory(Player player, HashMap<String, String> context) {
        // Decide whether warnings need to be suppressed
        suppressWarnings = MainConfig.get().getMiscellaneous().SUPPRESS_GUI_WARNINGS;
        hpi = ConfigInventories.get();
        // Get the default icons
        icons = new Icon[hpi.getInteger("inventories." + getId() + ".size")];
        // Get the unique ID of the player, never store the player object
        uuid = player.getUniqueId();
        // Get the icon list
        String items = hpi.getString("inventories." + getId() + ".icons");
        // Count the amount of contents that will appear in
        int contentsPerPage = HPUtils.matchCount(CachedValues.CONTENT_PATTERN.matcher(items));
        contents = new PagedLists<>(transformContents(context, player), contentsPerPage);
        new BukkitRunnable() {
            @Override
            public void run() {
                build(context, player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new NewInventoryEvent(player, getInventory());
                        player.openInventory(getInventory());
                    }
                }.runTask(hp);
            }
        }.runTaskAsynchronously(hp);
        hp.getServer().getPluginManager().registerEvents(this, hp);
    }

    public abstract String getDefaultTitle();

    public abstract String getDefaultItems();

    public abstract String getId();

    public void build(HashMap<String, String> context, Player player) {
        int totalPages = contents.getTotalPages();
        String currentPage = context.get("page");
        String title = PAGE.matcher(hpi.getString("inventories." + getId() + ".title")).replaceAll(currentPage);
        title = PAGES.matcher(title).replaceAll(String.valueOf(totalPages));
        title = SECTION.matcher(title).replaceAll(context.get("section") != null ? context.get("section") : "None");
        inventory = Bukkit.createInventory(this,
                hpi.getInteger("inventories." + getId() + ".size"),
                title);
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
                Class<? extends Icon> iconClass = InventoryManager.cachedIcons.get(c).getIcon();
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
                        if (!suppressWarnings) {
                            hp.getLogger().warning("Illegal icon character " + c + " has been replaced with air.");
                        }
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            icon.item.setAmount(1);
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
            for (int i = 0; i < 46; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null) {
                    if (PersistenceManager.get().isIcon(item)) {
                        player.getInventory().setItem(i, new ItemStack(Material.AIR));
                    }
                }
            }
            IconClickEvent iconEvent = new IconClickEvent(player, icons[slot]);
            Bukkit.getPluginManager().callEvent(iconEvent);
            if (!iconEvent.isCancelled()) {
                iconEvent.setToDestroy(icons[slot].onClick((Player) event.getWhoClicked(), event));
            }
            if (iconEvent.willDestroy()) {
                destroy = true;
            }
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() != this) return;
        destroy((Player) event.getPlayer());
    }

    @EventHandler
    public void onInvNew(NewInventoryEvent event) {
        if (event.getInventory().getHolder() != this) return;
        if (destroy) destroy(event.getPlayer());
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

    public static class NewInventoryEvent extends Event implements Cancellable {

        private final UUID player;
        private boolean cancelled = false;
        private final Inventory inventory;

        public NewInventoryEvent(Player player, Inventory inventory) {
            this.player = player.getUniqueId();
            this.inventory = inventory;
            Bukkit.getPluginManager().callEvent(this);
        }

        private static final HandlerList HANDLERS = new HandlerList();

        public HandlerList getHandlers() {
            return HANDLERS;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        public Player getPlayer() {
            return Bukkit.getPlayer(this.player);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public Inventory getInventory() {
            return inventory;
        }
    }
}
