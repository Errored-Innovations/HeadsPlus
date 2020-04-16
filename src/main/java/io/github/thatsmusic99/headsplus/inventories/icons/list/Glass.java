package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.inventories.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Glass extends Icon {
    public Glass(Player player) {
        super(player);
    }

    public Glass() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        return false;
    }


    @Override
    public String getId() {
        return "glass";
    }

    @Override
    public String getDefaultMaterial() {
        return HeadsPlus.getInstance().getNMSVersion().getOrder() > 8 ?  "LIGHT_GRAY_STAINED_GLASS_PANE" : "STAINED_GLASS_PANE";
    }

    @Override
    public int getDefaultDataValue() {
        return 8;
    }

    @Override
    public String getDefaultDisplayName() {
        return "&c";
    }

    @Override
    public String[] getDefaultLore() {
        return new String[0];
    }
}
