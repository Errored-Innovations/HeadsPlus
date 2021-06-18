package io.github.thatsmusic99.headsplus.nms;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;

@Deprecated
public interface NMSManager {

    String getSkullOwnerName(SkullMeta m);

    default ShapelessRecipe getRecipe(ItemStack i, String name) {
        return new ShapelessRecipe(i);
    }

    default ShapedRecipe getShapedRecipe(ItemStack i, String name) {
        return new ShapedRecipe(i);
    }

    OfflinePlayer getOfflinePlayer(String name);

    Player getPlayer(String name);

    ItemStack getItemInHand(Player p);

    default ItemStack getSkullMaterial(int amount) {
        return new ItemStack(Material.getMaterial("SKULL_ITEM"), amount, (byte) 3);
    }

    String getNMSVersion();

    default ItemStack getSkull(int data) {
        return new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) data);
    }

    default boolean isSkull(ItemStack item) {
        return item.getItemMeta() instanceof SkullMeta;
    }

    HashMap<String, String> getNBTTags(ItemStack item);

}
