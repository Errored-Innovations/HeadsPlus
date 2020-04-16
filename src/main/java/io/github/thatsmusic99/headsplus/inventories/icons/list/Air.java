package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Air extends Icon {
    public Air(Player player) {
        super(player);
    }

    public Air() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        return false;
    }

    @Override
    public String getId() {
        return "air";
    }

    @Override
    public void initNameAndLore(String id, Player player) {
    }

    @Override
    public String getDefaultMaterial() {
        return "AIR";
    }

    @Override
    public int getDefaultDataValue() {
        return 0;
    }

    @Override
    public String getDefaultDisplayName() {
        return "";
    }

    @Override
    public String[] getDefaultLore() {
        return new String[0];
    }

}
