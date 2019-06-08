package io.github.thatsmusic99.headsplus.nms.v1_8_R3_NMS;

import com.mojang.authlib.GameProfile;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.nms.SearchGUI;
import io.github.thatsmusic99.headsplus.util.AdventCManager;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class v1_8_R3_NMS implements NMSManager {

    @Override
    public SearchGUI getSearchGUI(Player p, SearchGUI.AnvilClickEventHandler a) {
        return new SearchGUI1_8_R3(p, a);
    }

    @Override
    public String getSkullOwnerName(SkullMeta m) {
        return m.getOwner();
    }

    @Override
    public ShapelessRecipe getRecipe(org.bukkit.inventory.ItemStack i, String name) {
        return new ShapelessRecipe(i);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String name) {
        return Bukkit.getOfflinePlayer(name);
    }

    @Override
    public Player getPlayer(String name) {
        return Bukkit.getPlayer(name);
    }

    @Override
    public GameProfile getGameProfile(org.bukkit.inventory.ItemStack s) {
        EntityPlayer e = ((CraftPlayer) ((SkullMeta) s.getItemMeta()).getOwningPlayer().getPlayer()).getHandle();
        return e.getProfile();
    }

    @Override
    public org.bukkit.inventory.ItemStack getItemInHand(Player p) {
        return p.getItemInHand();
    }

    public org.bukkit.inventory.ItemStack getOffHand(Player p) {
        return null;
    }

    @Override
    public org.bukkit.inventory.ItemStack setCalendarValue(org.bukkit.inventory.ItemStack i, String value) {
        net.minecraft.server.v1_8_R3.ItemStack is = CraftItemStack.asNMSCopy(i);
        if (is == null) return i;
        if (is.getTag() == null) {
            is.setTag(new NBTTagCompound());
        }
        is.getTag().setString("advent-value", value);
        return CraftItemStack.asBukkitCopy(is);
    }

    @Override
    public AdventCManager getCalendarValue(org.bukkit.inventory.ItemStack is) {
        net.minecraft.server.v1_8_R3.ItemStack i = CraftItemStack.asNMSCopy(is);
        if (i == null) return null;
        if (i.getTag() != null) {
            return AdventCManager.valueOf(Objects.requireNonNull(i.getTag()).getString("advent-value"));
        }
        return null;
    }

    @Override
    public String getNMSVersion() {
        return "v1_8_R3";
    }

    @Override
    public org.bukkit.inventory.ItemStack setOpen(org.bukkit.inventory.ItemStack i, boolean value) {
        ItemStack is = CraftItemStack.asNMSCopy(i);
        if (is.getTag() == null) {
            is.setTag(new NBTTagCompound());
        }
        is.getTag().setBoolean("advent-open", value);
        return CraftItemStack.asBukkitCopy(is);
    }

    @Override
    public boolean isOpen(org.bukkit.inventory.ItemStack is) {
        ItemStack i = CraftItemStack.asNMSCopy(is);
        if (i.getTag() != null) {
            return Objects.requireNonNull(i.getTag()).getBoolean("advent-open");
        }
        return false;
    }

    @Override
    public HashMap<String, String> getNBTTags(org.bukkit.inventory.ItemStack item) {
        ItemStack i = CraftItemStack.asNMSCopy(item);
        HashMap<String, String> keys = new HashMap<>();
        if (i.getTag() != null) {
            for (String str : i.getTag().c()) {
                keys.put(str, i.getTag().get(str).toString());
            }
        }
        return keys;
    }

    @Override
    public Sound getEXPSound() {
        return Sound.valueOf("ORB_PICKUP");
    }
}
