package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class Favourites extends Icon {

    public Favourites(Player player) {
        super(player);
    }

    public Favourites() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        InventoryManager.getManager(player).open(InventoryManager.InventoryType.HEADS_FAVORITES, new HashMap<>());
        return true;
    }

    @Override
    public String getId() {
        return "favourites";
    }

}
