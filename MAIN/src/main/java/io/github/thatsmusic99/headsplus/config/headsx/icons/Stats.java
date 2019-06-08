package io.github.thatsmusic99.headsplus.config.headsx.icons;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.headsx.Icon;
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
        h.add(ChatColor.GREEN + "Total heads: {heads}");
        h.add(ChatColor.GREEN + "Total pages: {pages}");
        h.add(ChatColor.GREEN + "Total sections: {sections}");
        h.add(ChatColor.GREEN + "Current balance: {balance}");
        h.add(ChatColor.GREEN + "Current section: {section}");
        return h;
    }

    @Override
    public String getDefaultDisplayName() {
        return "&6&l[&e&lStats&6&l]";
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
