package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SellheadHead extends Content {

    private String type;

    public SellheadHead(ItemStack itemStack, String type) {
        super(itemStack);
    }

    public SellheadHead() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        return false;
    }

    @Override
    public String getId() {
        return "sellable-head";
    }

    @Override
    public String getDefaultDisplayName() {
        return null;
    }

    @Override
    public String[] getDefaultLore() {
        return new String[0];
    }
}
