package io.github.thatsmusic99.headsplus.config.headsx.icons;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.headsx.Icon;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Glass extends ItemStack implements Icon {

    @Override
    public String getIconName() {
        return "glass";
    }

    @Override
    public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @Override
    public Material getDefaultMaterial() {
        return HeadsPlus.getInstance().getNMS().getColouredBlock(MaterialTranslator.BlockType.STAINED_GLASS_PANE, 8).getType();
    }

    @Override
    public List<String> getDefaultLore() {
        return new ArrayList<>();
    }

    @Override
    public String getDefaultDisplayName() {
        return "&c";
    }

    @Override
    public List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    @Override
    public String getSingleLetter() {
        return "G";
    }
}
