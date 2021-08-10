package io.github.thatsmusic99.headsplus.managers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ProfileManager {

    public static CompletableFuture<String> getName(SkullMeta meta) {
        return CompletableFuture.supplyAsync(() -> {
                    GameProfile profile = getProfile(meta);
                    if (profile != null) return profile.getName();
                    return "";
        }, HeadsPlus.async).thenApplyAsync(name -> name, HeadsPlus.sync);
    }

    public static CompletableFuture<UUID> getUUID(SkullMeta meta) {
        return CompletableFuture.supplyAsync(() -> {
            GameProfile profile = getProfile(meta);
            if (profile != null) return profile.getId();
            return null;
        }, HeadsPlus.async).thenApplyAsync(id -> id, HeadsPlus.sync);
    }

    public static CompletableFuture<String> getB64Texture(SkullMeta meta) {
        return CompletableFuture.supplyAsync(() -> {
            GameProfile profile = getProfile(meta);
            if (profile != null) {
                Property texture = profile.getProperties().get("textures").iterator().next();
                return texture.getValue();
            }
            return "";
        }, HeadsPlus.async).thenApplyAsync(texture -> texture, HeadsPlus.sync);
    }

    public static CompletableFuture<String> getTextureURL(SkullMeta meta) {
        return CompletableFuture.supplyAsync(() -> {
            GameProfile profile = getProfile(meta);
            if (profile != null) {
                Property texture = profile.getProperties().get("textures").iterator().next();
                return new String(Base64.getDecoder().decode(texture.getValue().getBytes()));
            }
            return "";
        }, HeadsPlus.async).thenApplyAsync(texture -> texture, HeadsPlus.sync);
    }

    private static GameProfile getProfile(SkullMeta meta) {
        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            return (GameProfile) field.get(meta);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
