package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.api.events.SectionChangeEvent;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CustomHeadSection extends Content {

    private String section;

    public CustomHeadSection(ItemStack itemStack, String section) {
        super(itemStack);
        this.section = section;
    }

    public CustomHeadSection() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        HashMap<String, String> context = new HashMap<>();
        context.put("section", section);
        InventoryManager manager = InventoryManager.getManager(player);
        SectionChangeEvent changeEvent = new SectionChangeEvent(player, manager.getSection(), section);
        Bukkit.getPluginManager().callEvent(changeEvent);
        if (!changeEvent.isCancelled()) {
            InventoryManager.getManager(player).open(InventoryManager.InventoryType.HEADS_CATEGORY, context);
            return true;
        }
        // The event was cancelled, so don't destroy the GUI
        return false;

    }

    @Override
    public String getId() {
        return "headsection";
    }

}
