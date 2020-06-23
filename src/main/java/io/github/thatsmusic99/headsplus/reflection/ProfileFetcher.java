package io.github.thatsmusic99.headsplus.reflection;

import com.mojang.authlib.GameProfile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;

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
}
