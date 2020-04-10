package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.SectionChangeEvent;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ChallengeSection extends Content {
    public ChallengeSection(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        event.setCancelled(true);
        String section = HeadsPlus.getInstance().getNBTManager().getChallengeSection(event.getCurrentItem());
        InventoryManager manager = InventoryManager.getManager(player);
        SectionChangeEvent changeEvent = new SectionChangeEvent(player, null, section);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
        //    im.showChallengeSection(section);
        }
    }

    @Override
    public String getId() {
        return null;
    }
}
