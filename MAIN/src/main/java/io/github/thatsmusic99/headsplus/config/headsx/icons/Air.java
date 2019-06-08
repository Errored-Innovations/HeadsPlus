package io.github.thatsmusic99.headsplus.config.headsx.icons;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.headsx.Icon;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class Air implements Icon {
    // ABCDEFGHIJKLMNOPRSTVXZ
    @Override
    public String getIconName() {
        return "air";
    }

    @Override
    public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
        // Air doesn't do anything... but okay...
        e.setCancelled(true);
    }

    @Override
    public Material getDefaultMaterial() {
        return Material.AIR;
    }

    @Override
    public List<String> getDefaultLore() {
        return new ArrayList<>();
    }

    @Override
    public String getDefaultDisplayName() {
        // DOESN'T EVEN HAVE A DISPLAY NAME
        return "";
    }

    @Override
    public List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    @Override
    public String getSingleLetter() {
        return "A";
    }
}
