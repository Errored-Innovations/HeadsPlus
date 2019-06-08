package io.github.thatsmusic99.headsplus.config.headsx.icons;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.headsx.Icon;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Close extends ItemStack implements Icon {
    @Override
    public String getIconName() {
        return "close";
    }

    @Override
    public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
        e.setCancelled(true);
        p.closeInventory();
    }

    @Override
    public Material getDefaultMaterial() {
        return Material.BARRIER;
    }

    @Override
    public List<String> getDefaultLore() {
        return new ArrayList<>();
    }

    @Override
    public String getDefaultDisplayName() {
        return "&c&lClose Menu";
    }

    @Override
    public List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    @Override
    public String getSingleLetter() {
        return "X";
    }
}
