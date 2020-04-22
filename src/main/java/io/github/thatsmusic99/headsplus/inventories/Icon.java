package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Icon {

    protected static HeadsPlus hp = HeadsPlus.getInstance();
    protected static HeadsPlusMessagesManager hpc = hp.getMessagesConfig();
    protected FileConfiguration hpi;
    protected ItemStack item;
    private String id;

    public Icon(ItemStack itemStack) {
        item = itemStack;
        hpi = hp.getItems().getConfig();
    }

    public Icon(String id, Player player) {
        this(id);
        initNameAndLore(id, player);
    }

    public Icon(String id) {
        hpi = hp.getItems().getConfig();
        this.id = id;
        initItem(id);
    }
    public Icon(Player player) {
        hpi = hp.getItems().getConfig();
        initItem(getId());
        initNameAndLore(getId(), player);
    }

    public Icon() {

    }

    protected void initItem(String id) {
        try {
            item = new ItemStack(Material.valueOf(hpi.getString("icons." + id + ".material")),
                    1,
                    (byte) hpi.getInt("icons." + id + ".data-value"));

        } catch (NullPointerException ex) {
            hp.getLogger().warning("Null icon found for " + id + ", please check your inventories.yml and see if this icon actually exists! (Error code: 8)");
        }
    }

    public abstract boolean onClick(Player player, InventoryClickEvent event);

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return item;
    }

    public List<String> getLore() {
        return hpi.getStringList("icons." + getId() + ".lore");
    }

    public void initNameAndLore(String id, Player player) {
        ItemMeta meta = item.getItemMeta();
        try {
            meta.setDisplayName(hpc.formatMsg(hpi.getString("icons." + id + ".display-name"), player));
            List<String> lore = new ArrayList<>();
            for (String loreStr : hpi.getStringList("icons." + id + ".lore")) {
                lore.add(hpc.formatMsg(loreStr, player));
            }
            meta.setLore(lore);
        } catch (NullPointerException ex) {
            hp.getLogger().warning("There was a problem setting the display name or lore for icon " + id + "! (Error code: 9)");
        }
        item.setItemMeta(meta);
    }

    public abstract String getDefaultMaterial();

    public abstract int getDefaultDataValue();

    public abstract String getDefaultDisplayName();

    public abstract String[] getDefaultLore();
}
