package io.github.thatsmusic99.headsplus.nms;

import com.mojang.authlib.GameProfile;
import io.github.thatsmusic99.headsplus.util.AdventCManager;
import io.github.thatsmusic99.headsplus.util.MaterialTranslator;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;

public interface NMSManager {

    SearchGUI getSearchGUI(Player p, SearchGUI.AnvilClickEventHandler a);

    default SkullMeta setSkullOwner(String s, SkullMeta m) {
        m.setOwner(s);
        return m;
    }

    String getSkullOwnerName(SkullMeta m);

    ShapelessRecipe getRecipe(ItemStack i, String name);

    OfflinePlayer getOfflinePlayer(String name);

    Player getPlayer(String name);

    GameProfile getGameProfile(ItemStack s);

    ItemStack getItemInHand(Player p);

    default ItemStack getSkullMaterial(int amount) {
        return new ItemStack(Material.getMaterial("SKULL_ITEM"), amount, (byte) 3);
    }

    default ItemStack getColouredBlock(MaterialTranslator.BlockType b, int data) {
        if (b.equals(MaterialTranslator.BlockType.TERRACOTTA)) {
            return new ItemStack(Material.getMaterial("STAINED_CLAY"), 1, (byte) data);
        } else if (b.equals(MaterialTranslator.BlockType.STAINED_GLASS_PANE)) {
            return new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte) data);
        } else if (b.equals(MaterialTranslator.BlockType.WOOL)) {
            return new ItemStack(Material.getMaterial("WOOL"), 1, (byte) data);
        }
        return null;
    }

    default ItemStack getOffHand(Player p) {
        return p.getInventory().getItemInOffHand();
    }

    default Material getNewItems(MaterialTranslator.ChangedMaterials b) {
        if (b == MaterialTranslator.ChangedMaterials.FIREWORK_CHARGE) {
            return Material.getMaterial("FIREWORK_CHARGE");
        } else if (b == MaterialTranslator.ChangedMaterials.PORK) {
            return Material.getMaterial("PORK");
        } else if (b == MaterialTranslator.ChangedMaterials.SULPHUR){
            return Material.getMaterial("SULPHUR");
        } else if (b == MaterialTranslator.ChangedMaterials.GRILLED_PORK){
            return Material.getMaterial("GRILLED_PORK");
        } else {
            return Material.getMaterial("INK_SACK");
        }
    }

    ItemStack setCalendarValue(ItemStack i, String value);

    AdventCManager getCalendarValue(ItemStack i);

    String getNMSVersion();

    ItemStack setOpen(ItemStack i, boolean value);

    boolean isOpen(ItemStack is);

    default ItemStack getSkull(int data) {
        return new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) data);
    }

    HashMap<String, String> getNBTTags(ItemStack item);

    default Sound getEXPSound() {
        return Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP");
    }

}
