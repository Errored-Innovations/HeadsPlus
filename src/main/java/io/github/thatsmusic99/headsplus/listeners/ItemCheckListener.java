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

    @Override 
    public void onEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Bukkit.getScheduler().runTask(HeadsPlus.get(), () -> {
            ItemStack offhand = player.getInventory().getItemInOffHand();
            if (PersistenceManager.get().isIcon(offhand)) {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        });
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(InventoryClickEvent.class,
                this, EventPriority.HIGHEST,
                new HeadsPlusEventExecutor(InventoryClickEvent.class, "InventoryClickEvent", this), HeadsPlus.get());

    }
}
