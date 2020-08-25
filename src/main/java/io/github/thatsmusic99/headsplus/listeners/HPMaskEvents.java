package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.SellHead;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class HPMaskEvents extends HeadsPlusListener<InventoryClickEvent> {

    private static final HashMap<UUID, BukkitRunnable> maskMonitors = new HashMap<>();

    public HPMaskEvents() {
        super();
        Bukkit.getPluginManager().registerEvent(InventoryClickEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(InventoryClickEvent.class, "InventoryClickEvent"), HeadsPlus.getInstance());

        Bukkit.getPluginManager().registerEvent(EntityDamageEvent.class,
                new HeadsPlusListener<EntityDamageEvent>() {
                    @Override
                    public void onEvent(EntityDamageEvent event) {
                        if (event.getEntity() instanceof Player) {
                            Player player = (Player) event.getEntity();
                            if (Bukkit.getPlayer(player.getUniqueId()) == null) return; // Citizens NPC
                            HeadsPlus hp = HeadsPlus.getInstance();
                            if (hp.getConfiguration().getPerks().mask_powerups) {
                                HPPlayer pl = HPPlayer.getHPPlayer(player);
                                if (pl.isIgnoringFallDamage()) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }, EventPriority.NORMAL, new HeadsPlusEventExecutor(EntityDamageEvent.class, "EntityDamageEvent"), HeadsPlus.getInstance());

        Bukkit.getPluginManager().registerEvent(PlayerQuitEvent.class,
                new HeadsPlusListener<PlayerQuitEvent>() {
                    @Override
                    public void onEvent(PlayerQuitEvent event) {
                        if (maskMonitors.containsKey(event.getPlayer().getUniqueId())) {
                            HPPlayer player = HPPlayer.getHPPlayer(event.getPlayer());
                            player.clearAllMasks();
                            maskMonitors.remove(event.getPlayer().getUniqueId());
                        }
                    }
                }, EventPriority.NORMAL, new HeadsPlusEventExecutor(PlayerQuitEvent.class, "PlayerQuitEvent"), HeadsPlus.getInstance());


    }

    @EventHandler
    public void onEvent(InventoryClickEvent e) {
        HeadsPlus hp = HeadsPlus.getInstance();
        Player player = (Player) e.getWhoClicked();
        for (int i = 0; i < 46; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null) {
                if (NBTManager.isIcon(item)) {
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
            }
        }
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

        if (hp.getConfiguration().getPerks().mask_powerups) {
            if (e.getRawSlot() == getSlot() || (shift && e.getRawSlot() != getSlot())) {
                checkMask((Player) e.getWhoClicked(), item);
            }
        }
    }

    public static void checkMask(Player player, ItemStack item) {
        HeadsPlus hp = HeadsPlus.getInstance();
        ConfigurationSection maskSettings = hp.getConfiguration().getMechanics().getConfigurationSection("masks");
        int period = maskSettings.getInt("check-interval");
        int reset = maskSettings.getInt("reset-after-x-intervals");
        NMSManager nms = hp.getNMS();
        if (item != null) {
            if (nms.isSkull(item)) {
                String s = NBTManager.getType(item);
                if (SellHead.isRegistered(s)) {
                    HPPlayer pl = HPPlayer.getHPPlayer(player);
                    UUID uuid = player.getUniqueId();
                    if (maskMonitors.containsKey(uuid)) {
                        pl.clearMask();
                        maskMonitors.get(uuid).cancel();
                        maskMonitors.remove(uuid);
                    }
                    pl.addMask(s);
                    final String type = s;
                    boolean hasWG = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
                    maskMonitors.put(uuid, new BukkitRunnable() {

                        private int currentInterval = 0;
                        private boolean tempDisable = false;

                        @Override
                        public void run() {
                            ItemStack helmet = player.getInventory().getHelmet();
                            currentInterval++;
                            if (helmet == null
                                    || helmet.getType() == Material.AIR
                                    || !NBTManager.getType(helmet).equals(type)
                                    || !player.isOnline()) {
                                pl.clearMask();
                                maskMonitors.remove(uuid);
                                cancel();
                            } else if (!maskMonitors.containsKey(uuid)) {
                                pl.clearMask();
                                cancel();
                            } else if (hasWG && !FlagHandler.canUseMasks(player)) {
                                pl.tempClearMasks();
                                tempDisable = true;
                            } else if (currentInterval == reset || tempDisable) {
                                pl.refreshMasks();
                                currentInterval = 0;
                            }
                        }
                    });
                    maskMonitors.get(uuid).runTaskTimer(hp, period, period);
                }
            }
        }
    }

    private int getSlot() {
        NMSIndex nms = HeadsPlus.getInstance().getNMSVersion();
        if (nms.getOrder() == 9 || nms.getOrder() == 10) {
            return 39;
        } else {
            return 5;
        }
    }
}
