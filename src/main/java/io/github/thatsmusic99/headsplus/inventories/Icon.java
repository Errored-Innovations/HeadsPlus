package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigItems;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Icon {

    protected static HeadsPlus hp = HeadsPlus.getInstance();
    protected static HeadsPlusMessagesManager hpc = hp.getMessagesConfig();
    protected static FileConfiguration hpi = hp.getItems().getConfig();
    protected ItemStack item;

    public Icon(ItemStack itemStack) {
        item = itemStack;
    }

    public Icon() {
        item = new ItemStack(Material.valueOf(hpi.getString("icons." + getId() + ".material")),
                1,
                (byte) hpi.getInt("icons." + getId() + ".data-value"));
    }

    public abstract void onClick(Player player, InventoryClickEvent event);

    public abstract String getId();

    public ItemStack getItemStack() {
        return item;
    }

    public List<String> getLore() {
        return hpi.getStringList("icons." + getId() + ".lore");
    }
}
