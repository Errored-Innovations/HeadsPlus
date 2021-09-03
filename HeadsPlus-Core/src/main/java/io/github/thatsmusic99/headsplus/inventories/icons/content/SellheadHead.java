package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SellheadHead extends Content {

    private String type;

    public SellheadHead(ItemStack itemStack, String type) {
        super(itemStack);
        this.type = type;
    }

    public SellheadHead() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        if (event.isRightClick()) {
            player.performCommand("sellhead " + type + " 1");
        } else {
            player.performCommand("sellhead " + type);
        }
        return false;
    }

    @Override
    public String getId() {
        return "sellable-head";
    }

}
