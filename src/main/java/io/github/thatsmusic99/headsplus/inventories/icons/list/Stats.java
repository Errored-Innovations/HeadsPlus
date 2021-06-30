package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigInventories;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.inventories.Icon;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
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
        ConfigCustomHeads hpch = ConfigCustomHeads.get();
        InventoryManager manager = InventoryManager.getManager(player);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(hpc.formatMsg(ConfigInventories.get().getString("icons." + id + ".display-name"), player));
        List<String> lore = new ArrayList<>();
        for (String loreStr : ConfigInventories.get().getStringList("icons." + id + ".lore")) {
            HPUtils.parseLorePlaceholders(lore, hpc.formatMsg(loreStr, player),
                    new HPUtils.PlaceholderInfo("{head}", hpch.allHeadsCache.size(), true),
                    new HPUtils.PlaceholderInfo("{balance}", getBalance(player), hp.isVaultEnabled()),
                    new HPUtils.PlaceholderInfo("{sections}",  hpch.sections.size(), true),
                    new HPUtils.PlaceholderInfo("{section}", manager.getSection(), manager.getSection() != null),
                    new HPUtils.PlaceholderInfo("{pages}", totalPages, true));

        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private double getBalance(Player player) {
        try {
            return hp.getEconomy().getBalance(player);
        } catch (NoClassDefFoundError ex) {
            return 0.0;
        }
    }
}
