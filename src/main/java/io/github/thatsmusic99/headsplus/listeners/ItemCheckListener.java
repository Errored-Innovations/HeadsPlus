package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemCheckListener extends HeadsPlusListener<InventoryClickEvent> {

    @Override // TODO - move into a runnable
    public void onEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        for (int i = 0; i < 46; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;
            if (PersistenceManager.get().isIcon(item)) {
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
        }
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(InventoryClickEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(InventoryClickEvent.class, "InventoryClickEvent", this), HeadsPlus.get());

    }
}
