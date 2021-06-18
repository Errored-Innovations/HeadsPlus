package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigInventories;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.Icon;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Stats extends Icon {

    private int totalPages;

    public Stats() {}

    public Stats(Player player, Integer totalPages) {
        initItem("stats");
        this.totalPages = totalPages;
        initNameAndLore("stats", player);
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
        ConfigCustomHeads hpch = HeadsPlus.getInstance().getHeadsXConfig();
        InventoryManager manager = InventoryManager.getManager(player);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(hpc.formatMsg(ConfigInventories.get().getString("icons." + id + ".display-name"), player));
        List<String> lore = new ArrayList<>();
        for (String loreStr : ConfigInventories.get().getStringList("icons." + id + ".lore")) {
            lore.add(hpc.formatMsg(loreStr, player)
                    .replaceAll("\\{heads}", String.valueOf(hpch.allHeadsCache.size()))
                    .replaceAll("\\{balance}", hp.econ() ? String.valueOf(hp.getEconomy().getBalance(player)) : "None")
                    .replaceAll("\\{sections}", String.valueOf(hpch.sections.size()))
                    .replaceAll("\\{section}", manager.getSection() == null ? "None" : manager.getSection())
                    .replaceAll("\\{pages}", String.valueOf(totalPages)));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
