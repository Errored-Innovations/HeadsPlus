package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigInventories;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Icon {

    protected ItemStack item;
    private String id;

    public Icon(ItemStack itemStack) {
        item = itemStack;
        PersistenceManager.get().makeIcon(item);
    }

    public Icon(String id, Player player) {
        this(id);
        initNameAndLore(id, player);
    }

    public Icon(String id) {
        this.id = id;
        initItem(id);
        if (item != null) PersistenceManager.get().makeIcon(item);
    }

    public Icon(Player player) {
        initItem(getId());
        initNameAndLore(getId(), player);
        if (item != null) PersistenceManager.get().makeIcon(item);
    }

    public Icon() {

    }

    protected void initItem(String id) {
        try {
            item = new ItemStack(Material.valueOf(ConfigInventories.get().getString("icons." + id + ".material")));

        } catch (NullPointerException ex) {
            HeadsPlus.get().getLogger().warning("Null icon found for " + id + ", please check your inventories.yml " +
                    "and see if this icon actually exists!");
        }
    }

    public abstract boolean onClick(Player player, InventoryClickEvent event);

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return item;
    }

    public void initNameAndLore(String id, Player player) {
        ItemMeta meta = item.getItemMeta();
        try {
            meta.setDisplayName(MessagesManager.get().formatMsg(ConfigInventories.get().getString("icons." + id +
                    ".display-name"), player));
            List<String> lore = new ArrayList<>();
            for (String loreStr : ConfigInventories.get().getStringList("icons." + id + ".lore")) {
                lore.add(MessagesManager.get().formatMsg(loreStr, player));
            }
            meta.setLore(lore);
        } catch (NullPointerException ex) {
            HeadsPlus.get().getLogger().warning("There was a problem setting the display name or lore for icon " + id + "! (Error code: 9)");
        }
        item.setItemMeta(meta);
    }
}
