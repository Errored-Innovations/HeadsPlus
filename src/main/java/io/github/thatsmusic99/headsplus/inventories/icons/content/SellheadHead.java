package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SellheadHead extends Content {
    public SellheadHead(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        return false;
    }

    @Override
    public String getId() {
        return null;
    }
}
