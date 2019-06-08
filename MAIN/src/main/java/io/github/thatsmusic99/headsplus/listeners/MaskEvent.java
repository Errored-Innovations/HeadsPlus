package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.nms.v1_13_NMS.v1_13_NMS;
import io.github.thatsmusic99.headsplus.nms.v1_13_R2_NMS.v1_13_R2_NMS;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
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
            NMSManager nms = hp.getNMS();
            NBTManager nbt = hp.getNBTManager();
            if (e.getRawSlot() == getSlot()) {

                ItemStack ist = e.getCursor();
                if (ist != null) {
                    if (ist.getType().equals(nms.getSkull(3).getType())) {
                        HeadsPlusConfigHeads hpch = hp.getHeadsConfig();
                        String s = nbt.getType(ist).toLowerCase();
                        if (hpch.mHeads.contains(s) || hpch.uHeads.contains(s) || s.equalsIgnoreCase("player")) {
                            HPPlayer pl = HPPlayer.getHPPlayer((OfflinePlayer) e.getWhoClicked());
                            pl.addMask(s);
                            maskMonitors.put((Player) e.getWhoClicked(), new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (e.getWhoClicked().getInventory().getHelmet() == null) {
                                        HPPlayer pl = HPPlayer.getHPPlayer((OfflinePlayer) e.getWhoClicked());
                                        pl.clearMask();
                                        maskMonitors.remove(e.getWhoClicked());
                                        cancel();
                                    }
                                }
                            });
                            maskMonitors.get(e.getWhoClicked()).runTaskTimer(HeadsPlus.getInstance(), 20, 40);
                        }
                    }
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
        NMSManager nms = HeadsPlus.getInstance().getNMS();
        if (nms instanceof v1_13_R2_NMS || nms instanceof v1_13_NMS) {
            return 39;
        } else {
            return 5;
        }
    }
}
