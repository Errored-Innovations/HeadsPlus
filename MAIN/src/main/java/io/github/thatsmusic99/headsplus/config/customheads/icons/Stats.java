package io.github.thatsmusic99.headsplus.config.customheads.icons;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.customheads.Icon;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Stats extends ItemStack implements Icon {
    @Override
    public String getIconName() {
        return "stats";
    }

    @Override
    public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public Material getDefaultMaterial() {
        return Material.PAPER;
    }

    @Override
    public List<String> getDefaultLore() {
        List<String> h = new ArrayList<>();
        h.add(ChatColor.GREEN + "{msg_inventory.icon.stats.total-heads} {heads}");
        h.add(ChatColor.GREEN + "{msg_inventory.icon.stats.total-pages} {pages}");
        h.add(ChatColor.GREEN + "{msg_inventory.icon.stats.total-sections} {sections}");
        h.add(ChatColor.GREEN + "{msg_inventory.icon.stats.current-balance} {balance}");
        h.add(ChatColor.GREEN + "{msg_inventory.icon.stats.current-section} {section}");
        return h;
    }

    @Override
    public String getDefaultDisplayName() {
        return "{msg_inventory.icon.stats.icon}";
    }
    @Override
    public List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    @Override
    public String getSingleLetter() {
        return "S";
    }

}
