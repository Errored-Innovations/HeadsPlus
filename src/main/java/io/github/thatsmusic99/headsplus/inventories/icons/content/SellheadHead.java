package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.config.ConfigInventories;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.managers.SellableHeadsManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SellheadHead extends Content {

    private String type;

    public SellheadHead(ItemStack itemStack, String type) {
        super(itemStack);
        this.type = type;
    }

    public SellheadHead() {
    }

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
    public void initNameAndLore(String id, Player player) {

        // Start building the lore
        List<String> lore = new ArrayList<>();

        // Go through each line and replace price tags
        for (String loreStr : ConfigInventories.get().getStringList("icons.sellable-head.lore")) {
            HPUtils.parseLorePlaceholders(lore, MessagesManager.get().formatMsg(loreStr, player),
                    new HPUtils.PlaceholderInfo("{price}", SellableHeadsManager.get().getPrice(id), true));
        }

        // Set the lore
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    @Override
    public String getId() {
        return "sellable-head";
    }

}
