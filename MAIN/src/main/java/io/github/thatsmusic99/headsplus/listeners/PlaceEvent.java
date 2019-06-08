package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.nms.NewNMSManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceEvent implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        try {
            HeadsPlus hp = HeadsPlus.getInstance();
            if (hp.isStoppingPlaceableHeads()) {
                if (hp.getNMS() instanceof NewNMSManager) {
                    if (e.getItemInHand().getType() == ((NewNMSManager) hp.getNMS()).getWallSkull()) {
                        if (!e.getPlayer().hasPermission("headsplus.bypass.preventplacement")) {
                            if (hp.getNBTManager().isSellable(e.getItemInHand())) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(hp.getMessagesConfig().getString("block-place-denied"));
                            }
                        }
                    }
                }
                if (e.getItemInHand().getType() == hp.getNMS().getSkullMaterial(1).getType() ) {
                    if (!e.getPlayer().hasPermission("headsplus.bypass.preventplacement")) {
                        if (hp.getNBTManager().isSellable(e.getItemInHand())) {
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(hp.getMessagesConfig().getString("block-place-denied"));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            new DebugPrint(ex, "Event (PlaceEvent)", false, null);
        }
    }
}
