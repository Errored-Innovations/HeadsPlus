package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.api.events.SectionChangeEvent;
import io.github.thatsmusic99.headsplus.config.ConfigInventories;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChallengeSection extends Content {

    private io.github.thatsmusic99.headsplus.api.ChallengeSection section;

    public ChallengeSection(io.github.thatsmusic99.headsplus.api.ChallengeSection section) {
        super(new ItemStack(section.getMaterial(), 1, section.getMaterialData()));
        this.section = section;
    }

    public ChallengeSection() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        event.setCancelled(true);
        HashMap<String, String> context = new HashMap<>();
        context.put("section", section.getName());
        InventoryManager manager = InventoryManager.getManager(player);
        SectionChangeEvent changeEvent = new SectionChangeEvent(player, null, section.getName());
        Bukkit.getPluginManager().callEvent(changeEvent);
        if (!changeEvent.isCancelled()) {
            manager.open(InventoryManager.InventoryType.CHALLENGES_LIST, context);
        }
        return true;
    }

    @Override
    public String getId() {
        return "challenges-section";
    }

    @Override
    public void initNameAndLore(String id, Player player) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(hpc.formatMsg(ConfigInventories.get().getString("icons.challenges-section.display-name")
                .replaceAll("\\{section-name}", section.getDisplayName()), player));
        List<String> lore = new ArrayList<>();
        for (String loreStr : ConfigInventories.get().getStringList("icons.challenges-section.lore")) {
            if (loreStr.contains("{section-lore}")) {
                for (String loreStr2 : section.getLore()) {
                    lore.add(hpc.formatMsg(loreStr2, player));
                }
            } else {
                lore.add(hpc.formatMsg(loreStr, player).replaceAll("(\\{challenge-count}|\\{challenges})", String.valueOf(section.getChallenges().size())));
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

}
