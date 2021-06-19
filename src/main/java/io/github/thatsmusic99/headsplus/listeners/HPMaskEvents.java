package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.SellHead;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class HPMaskEvents extends HeadsPlusListener<InventoryClickEvent> {

    private static final HashMap<UUID, BukkitRunnable> maskMonitors = new HashMap<>();

    @EventHandler
    public void onEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        for (int i = 0; i < 46; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;
            if (PersistenceManager.get().isIcon(item)) {
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
        }
        if (!MainConfig.get().getMainFeatures().MASKS) return;
        ItemStack item;
        boolean shift = e.isShiftClick();
        // If we're shift clicking
        if (shift) {
            // We need to get the current item
            item = e.getCurrentItem();
            ItemStack currHelmet = e.getWhoClicked().getInventory().getHelmet();
            if (!(currHelmet == null || currHelmet.getType().equals(Material.AIR))) return;
        } else {
            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) return;
            item = e.getCursor();
        }

        if (e.getRawSlot() == 5 || (shift && e.getRawSlot() != 5)) {
            checkMask((Player) e.getWhoClicked(), item);
        }
    }

    @Override
    public void init() {
        HeadsPlusListener<?> listener;
        Bukkit.getPluginManager().registerEvent(InventoryClickEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(InventoryClickEvent.class, "InventoryClickEvent", this), HeadsPlus.getInstance());

        Bukkit.getPluginManager().registerEvent(EntityDamageEvent.class,
                listener = new HeadsPlusListener<EntityDamageEvent>() {
                    @Override
                    public void onEvent(EntityDamageEvent event) {
                        if (event.getEntity() instanceof Player) {
                            Player player = (Player) event.getEntity();
                            if (Bukkit.getPlayer(player.getUniqueId()) == null) return; // Citizens NPC
                            HeadsPlus hp = HeadsPlus.getInstance();
                            if (hp.getConfiguration().getPerks().mask_powerups) {
                                HPPlayer pl = HPPlayer.getHPPlayer(player);
                                if (pl.isIgnoringFallDamage() && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }, EventPriority.NORMAL, new HeadsPlusEventExecutor(EntityDamageEvent.class, "EntityDamageEvent", listener), HeadsPlus.getInstance());

        Bukkit.getPluginManager().registerEvent(PlayerQuitEvent.class,
                listener = new HeadsPlusListener<PlayerQuitEvent>() {
                    @Override
                    public void onEvent(PlayerQuitEvent event) {
                        if (maskMonitors.containsKey(event.getPlayer().getUniqueId())) {
                            HPPlayer player = HPPlayer.getHPPlayer(event.getPlayer());
                            player.clearAllMasks();
                            maskMonitors.remove(event.getPlayer().getUniqueId());
                        }
                    }
                }, EventPriority.NORMAL, new HeadsPlusEventExecutor(PlayerQuitEvent.class, "PlayerQuitEvent", listener), HeadsPlus.getInstance());

    }

    public static void checkMask(Player player, ItemStack item) {
        HeadsPlus hp = HeadsPlus.getInstance();
        int period = MainConfig.get().getMasks().CHECK_INTERVAL;
        if (item == null || !(item.getItemMeta() instanceof SkullMeta)) return;
        String s = PersistenceManager.get().getSellType(item);
        if (!SellHead.isRegistered(s)) return;
        HPPlayer pl = HPPlayer.getHPPlayer(player);
        UUID uuid = player.getUniqueId();
        if (maskMonitors.containsKey(uuid)) {
            pl.clearMask();
            maskMonitors.get(uuid).cancel();
            maskMonitors.remove(uuid);
        }
        pl.addMask(s);
        maskMonitors.put(uuid, new MaskRunnable(player, s));
        maskMonitors.get(uuid).runTaskTimer(hp, period, period);
    }

    private static class MaskRunnable extends BukkitRunnable {

        private int currentInterval = 0;
        private boolean tempDisable = false;
        private HPPlayer hpPlayer;
        private Player player;
        private String type;

        public MaskRunnable(Player player, String type) {
            this.player = player;
            this.hpPlayer = HPPlayer.getHPPlayer(player);
            this.type = type;
        }

        @Override
        public void run() {
            ItemStack helmet = player.getInventory().getHelmet();
            currentInterval++;
            if (helmet == null
                    || helmet.getType() == Material.AIR
                    || !PersistenceManager.get().getSellType(helmet).equals(type)
                    || !player.isOnline()) {
                hpPlayer.clearMask();
                maskMonitors.remove(player.getUniqueId());
                cancel();
            } else if (!maskMonitors.containsKey(player.getUniqueId())) {
                hpPlayer.clearMask();
                cancel();
            } else if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard") && !FlagHandler.canUseMasks(player)) {
                hpPlayer.tempClearMasks();
                tempDisable = true;
            } else if (currentInterval == MainConfig.get().getMasks().RESET_INTERVAL || tempDisable) {
                hpPlayer.refreshMasks();
                currentInterval = 0;
            }
        }
    }
}
