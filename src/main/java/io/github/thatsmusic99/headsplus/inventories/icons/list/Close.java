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

    @Override
    public String getDefaultMaterial() {
        return "BARRIER";
    }

    @Override
    public int getDefaultDataValue() {
        return 0;
    }

    @Override
    public String getDefaultDisplayName() {
        return "{msg_inventory.icon.close}";
    }

    @Override
    public String[] getDefaultLore() {
        return new String[0];
    }
}
