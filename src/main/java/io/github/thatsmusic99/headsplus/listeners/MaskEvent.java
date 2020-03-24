package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class MaskEvent implements Listener {

    private static HashMap<Player, BukkitRunnable> maskMonitors = new HashMap<>();

    @EventHandler
    public void onMaskPutOn(InventoryClickEvent e) {
        HeadsPlus hp = HeadsPlus.getInstance();
        if (hp.getConfiguration().getPerks().mask_powerups) {
            if (e.getRawSlot() == getSlot()) {

                ItemStack ist = e.getCursor();
                checkMask((Player) e.getWhoClicked(), ist);
            }
        }
    }

    public static void checkMask(Player player, ItemStack item) {
        HeadsPlus hp = HeadsPlus.getInstance();
        NMSManager nms = hp.getNMS();
        if (item != null) {
            if (item.getType().equals(nms.getSkull(3).getType())) {
                HeadsPlusConfigHeads hpch = hp.getHeadsConfig();
                String s = NBTManager.getType(item).toLowerCase();
                if (hpch.mHeads.contains(s) || hpch.uHeads.contains(s) || s.equalsIgnoreCase("player")) {
                    HPPlayer pl = HPPlayer.getHPPlayer(player);
                    pl.addMask(s);
                    maskMonitors.put(player, new BukkitRunnable() {
                        @Override
                        public void run() {
                            ItemStack helmet = player.getInventory().getHelmet();
                            if (helmet == null
                                    || helmet.getType() == Material.AIR
                                    || !NBTManager.getType(helmet).equals(s)
                                    || !player.isOnline()) {
                                pl.clearMask(s);
                                maskMonitors.remove(player);
                                cancel();
                            } else {
                                pl.refreshMasks();
                            }
                        }
                    });
                    maskMonitors.get(player).runTaskTimer(hp, 20, 60);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            HeadsPlus hp = HeadsPlus.getInstance();
            if (hp.getConfiguration().getPerks().mask_powerups) {
                HPPlayer pl = HPPlayer.getHPPlayer((Player) e.getEntity());
                if (pl.isIgnoringFallDamage()) {
                    e.setCancelled(true);
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
