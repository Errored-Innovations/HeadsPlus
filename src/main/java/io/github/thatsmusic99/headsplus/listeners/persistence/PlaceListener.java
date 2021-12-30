package io.github.thatsmusic99.headsplus.listeners.persistence;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class PlaceListener extends HeadsPlusListener<BlockPlaceEvent> {
    @Override
    public void onEvent(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (!(block.getState() instanceof Skull)) return;
        Skull skull = (Skull) block.getState();
        ItemMeta meta = event.getItemInHand().getItemMeta();
        PersistenceManager.get().copyStorageToSkull(meta, skull);
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(BlockPlaceEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(BlockPlaceEvent.class, "BlockPlaceEvent", this), HeadsPlus.get(), true);
    }
}
