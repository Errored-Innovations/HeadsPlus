package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomHeadSection extends Content {
    public CustomHeadSection(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        String section = NBTManager.getSection(item);
        HashMap<String, String> context = new HashMap<>();
        context.put("section", section);
        InventoryManager.getManager(player).open(InventoryManager.InventoryType.HEADS_CATEGORY, context);
    }

    @Override
    public String getId() {
        return "custom-head-section";
    }
}
