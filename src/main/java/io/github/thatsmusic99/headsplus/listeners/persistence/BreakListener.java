package io.github.thatsmusic99.headsplus.listeners.persistence;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Item;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class BreakListener extends HeadsPlusListener<BlockDropItemEvent> {
    @Override
    public void onEvent(BlockDropItemEvent event) {
        BlockState block = event.getBlockState();
        if (!(block instanceof Skull)) return;
        Skull skull = (Skull) block;
        for (Item item : event.getItems()){
            ItemStack stack = item.getItemStack();
            if (!(stack.getItemMeta() instanceof SkullMeta)) continue;
            PersistenceManager.get().copyStorageToItem(skull, stack);
            item.setItemStack(stack);
            break;
        }
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(BlockDropItemEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(BlockDropItemEvent.class, "BlockBreakEvent", this), HeadsPlus.get(), true);
    }
}
