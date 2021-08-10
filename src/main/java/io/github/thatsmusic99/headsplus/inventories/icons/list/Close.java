package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Close extends Icon {
    public Close(Player player) {
        super(player);
    }

    public Close() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        player.closeInventory();
        return true;
    }

    @Override
    public String getId() {
        return "close";
    }

}
