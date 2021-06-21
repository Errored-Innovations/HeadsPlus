package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.SkullMeta;

public class BlockPlaceListener extends HeadsPlusListener<BlockPlaceEvent> {

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(BlockPlaceEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(BlockPlaceEvent.class, "BlockPlaceEvent", this), HeadsPlus.get(), true);
        addPossibleData("stopping-heads", "true", "false");
        addPossibleData("is-a-skull", "true", "false");
        addPossibleData("can-bypass", "true", "false");
        addPossibleData("is-sellable", "true", "false");
        addPossibleData("player", "<Player>");
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getSellingHeads().STOP_PLACEMENT;
    }

    public void onEvent(BlockPlaceEvent e) {
        addData("player", e.getPlayer().getName());

        if (!addData("is-a-skull", e.getItemInHand().getItemMeta() instanceof SkullMeta)) return;
        if (addData("can-bypass", e.getPlayer().hasPermission("headsplus.bypass.preventplacement"))) return;
        if (!addData("is-sellable", PersistenceManager.get().isSellable(e.getItemInHand()))) return;
        e.setCancelled(true);
        HeadsPlusMessagesManager.get().sendMessage("event.block-place-denied", e.getPlayer());
    }
}
