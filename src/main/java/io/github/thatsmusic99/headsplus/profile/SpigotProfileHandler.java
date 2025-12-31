package io.github.thatsmusic99.headsplus.profile;

import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

/**
 * Targeted at Spigot 1.19+ (or whenever they added the profile API)
 */
@SuppressWarnings("deprecation")
public class SpigotProfileHandler implements IProfileHandler {

    @Override
    public boolean canUse() {

        try {
            Class.forName("org.bukkit.profile.PlayerTextures");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    @Override
    public String getName(@NotNull Skull skull) {
        if (skull.getOwnerProfile() == null) return null;
        return skull.getOwnerProfile().getName();
    }

    @Override
    public String getName(@NotNull SkullMeta meta) {
        if (meta.getOwnerProfile() == null) return null;
        return meta.getOwnerProfile().getName();
    }

    @Nullable
    @Override
    public String getName(@NotNull Entity mannequin) {
        if (!(mannequin instanceof Mannequin)) {
            throw new IllegalArgumentException("This method can only be used on Mannequins!");
        }

        PlayerProfile profile;
        try {
            profile = getProfile(mannequin);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (profile == null) return null;

        return profile.getName();
    }

    @Override
    public String getTexture(@NotNull Skull skull) {

        // Make sure there's a profile
        if (skull.getOwnerProfile() == null) return null;

        // Make sure there's textures
        PlayerTextures textures = skull.getOwnerProfile().getTextures();
        return getTexture(textures);
    }

    @Override
    public String getTexture(@NotNull SkullMeta meta) {
        if (meta.getOwnerProfile() == null) return null;
        return getTexture(meta.getOwnerProfile().getTextures());
    }

    @Nullable
    @Override
    public String getTexture(@NotNull OfflinePlayer player) {

        // uh oh reflection time
        Object profile;

        // Try the field first
        try {
            final Field profileField = player.getClass().getField("profile");
            profileField.setAccessible(true);
            profile = profileField.get(player);
        } catch (NoSuchFieldException | IllegalAccessException e) {

            // Try the method
            final Method profileMethod;
            try {
                profileMethod = player.getClass().getMethod("getProfile");
                profileMethod.setAccessible(true);
                profile = profileMethod.invoke(player);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        Object propertyMap;

        try {
            final Method propertiesMethod = profile.getClass().getMethod("properties");
            propertyMap = propertiesMethod.invoke(profile);
        } catch (NoSuchMethodException e) {
            final Method propertiesMethod;
            try {
                propertiesMethod = profile.getClass().getMethod("getProperties");
                propertyMap = propertiesMethod.invoke(profile);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        try {

            final Method textureMethod = propertyMap.getClass().getMethod("get", Object.class);
            final Collection<Object> properties = (Collection<Object>) textureMethod.invoke(propertyMap, "textures");
            final Object property = properties.iterator().next();

            final Field value = property.getClass().getDeclaredField("value");
            value.setAccessible(true);

            return HPUtils.toBase64Texture((String) value.get(property));
        } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    @Override
    public String getEntityTexture(@NotNull Entity mannequin) {
        if (!(mannequin instanceof Mannequin)) {
            throw new IllegalArgumentException("This method can only be used on Mannequins!");
        }

        PlayerProfile profile;
        try {
            profile = getProfile(mannequin);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (profile == null) return null;
        return getTexture(profile.getTextures());
    }

    private PlayerProfile getProfile(@NotNull Entity entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = entity.getClass().getMethod("getPlayerProfile");
        return (PlayerProfile) method.invoke(entity);
    }

    private @Nullable String getTexture(@NotNull PlayerTextures textures) {

        if (textures.isEmpty()) return null;
        URL skinUrl = textures.getSkin();
        if (skinUrl == null) return null;

        // Set up the texture
        return HPUtils.toBase64Texture(skinUrl.toString());
    }

    @Override
    public void copyProfile(@NotNull Skull skull, @NotNull SkullMeta meta) {
        meta.setOwnerProfile(skull.getOwnerProfile());
    }

    @Override
    public void forceSetProfile(@NotNull SkullMeta meta, @NotNull String name) {
        PlayerProfile profile = Bukkit.getServer().createPlayerProfile(HPUtils.getUUID(name), name);
        meta.setOwnerProfile(profile);
    }

    @Override
    public void forceSetProfileTexture(@NotNull SkullMeta meta, @NotNull String name, @NotNull String texture) throws MalformedURLException {
        PlayerProfile profile = Bukkit.getServer().createPlayerProfile(UUID.nameUUIDFromBytes(texture.getBytes()), name);
        profile.getTextures().setSkin(HPUtils.toSkinURL(texture));
        meta.setOwnerProfile(profile);
    }
}
