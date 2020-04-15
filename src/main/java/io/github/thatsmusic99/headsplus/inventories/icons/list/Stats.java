package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.HeadsPlusConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.Icon;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Stats extends Icon {

    public Stats(Player player) {
        super(player);
    }

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        return false;
    }

    @Override
    public String getId() {
        return "stats";
    }

    @Override
    public void initNameAndLore(String id, Player player) {
        HeadsPlusConfigCustomHeads hpch = HeadsPlus.getInstance().getHeadsXConfig();
        InventoryManager manager = InventoryManager.getManager(player);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(hpc.formatMsg(hpi.getString("icons." + id + ".display-name"), player));
        List<String> lore = new ArrayList<>();
        for (String loreStr : hpi.getStringList("icons." + id + ".lore")) {
            lore.add(hpc.formatMsg(loreStr, player)
                    .replaceAll("\\{heads}", String.valueOf(hpch.allHeadsCache.size()))
                    .replaceAll("\\{balance}", String.valueOf(hp.getEconomy().getBalance(player)))
                    .replaceAll("\\{sections}", String.valueOf(hpch.sections.size()))
                    .replaceAll("\\{section}", manager.getSection() == null ? "None" : manager.getSection()));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
