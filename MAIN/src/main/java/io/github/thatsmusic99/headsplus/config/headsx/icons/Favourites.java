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

public class Favourites extends ItemStack implements Icon {
    @Override
    public String getIconName() {
        return "favourites";
    }

    @Override
    public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
        e.setCancelled(true);
        im.showScreen(InventoryManager.Type.LIST_FAVORITES);
    }

    @Override
    public Material getDefaultMaterial() {
        return Material.DIAMOND;
    }

    @Override
    public List<String> getDefaultLore() {
        return new ArrayList<>();
    }

    @Override
    public String getDefaultDisplayName() {
        return "&b&lFavourites";
    }

    @Override
    public List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    @Override
    public String getSingleLetter() {
        return "F";
    }
}
