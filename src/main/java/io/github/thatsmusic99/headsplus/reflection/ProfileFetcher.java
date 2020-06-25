package io.github.thatsmusic99.headsplus.reflection;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProfileFetcher {

    public static GameProfile getProfile(ItemStack item) throws IllegalAccessException {
        SkullMeta meta = (SkullMeta) item.getItemMeta();
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
