package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class HPBlockPlaceEvent extends HeadsPlusListener<BlockPlaceEvent> {

    public HPBlockPlaceEvent() {
        super();
        Bukkit.getPluginManager().registerEvent(BlockPlaceEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(BlockPlaceEvent.class, "BlockPlaceEvent"), HeadsPlus.getInstance());
    }

    public void onEvent(BlockPlaceEvent e) {
        HeadsPlus hp = HeadsPlus.getInstance();
        if (hp.isStoppingPlaceableHeads()) {
            if (hp.getNMS().isSkull(e.getItemInHand())) {
                if (!e.getPlayer().hasPermission("headsplus.bypass.preventplacement")) {
                    if (NBTManager.isSellable(e.getItemInHand())) {
                        e.setCancelled(true);
                        hp.getMessagesConfig().sendMessage("event.block-place-denied", e.getPlayer());
                    }
                }
            }
        }
    }
}
