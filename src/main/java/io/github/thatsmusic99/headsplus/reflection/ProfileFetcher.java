package io.github.thatsmusic99.headsplus.reflection;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ProfileFetcher {

    public static GameProfile getProfile(Skull skull) throws IllegalAccessException {
        Field profile;
        try {
            profile = skull.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
        profile.setAccessible(true);
        return (GameProfile) profile.get(skull);
    }

    public static GameProfile getProfile(ItemStack item) throws IllegalAccessException {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        return getProfile(meta);
    }

    public static GameProfile getProfile(SkullMeta meta) throws IllegalAccessException {
        Field profile;
        try {
            profile = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
        profile.setAccessible(true);
        return (GameProfile) profile.get(meta);
    }

    public static GameProfile getProfile(OfflinePlayer player) {
        try {
            Method method = player.getClass().getMethod("getProfile");
            return (GameProfile) method.invoke(player);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SkullMeta setProfile(SkullMeta meta, String name) {
        UUID uuid;
        if (Bukkit.getPlayer(name) != null) {
            uuid = Bukkit.getPlayer(name).getUniqueId();
        } else {
            uuid = UUID.nameUUIDFromBytes(name.getBytes());
        }
        GameProfile profile = new GameProfile(uuid, name);
        return setProfile(meta, profile);
    }

    public static SkullMeta setProfile(SkullMeta meta, GameProfile profile) {
        try {
            Method profileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            profileMethod.setAccessible(true);
            profileMethod.invoke(meta, profile);
        } catch (NoSuchMethodException e) {
            Field profileField;
            try {
                profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, profile);
            } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
                noSuchFieldException.printStackTrace();
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return meta;
    }

    public static <T> T getHandle(Player player) {
        try {
            Method handle = player.getClass().getDeclaredMethod("getHandle");
            // It'll be fiiiine
            return (T) handle.invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
