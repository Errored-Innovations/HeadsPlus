package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Close extends Icon {
    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        player.closeInventory();
    }

    @Override
    public String getId() {
        return "close";
    }
}
