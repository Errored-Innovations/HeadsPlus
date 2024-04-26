package io.github.thatsmusic99.headsplus.profile;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Targeted at Authlib versions 3.x until 4.x
 */
public class AuthlibProfileHandler implements IProfileHandler {

    @Override
    public boolean canUse() {
        return true;
    }

    @Nullable
    @Override
    public String getName(@NotNull Skull skull) {
        return skull.getOwner();
    }

    @Nullable
    @Override
    public String getName(@NotNull SkullMeta meta) {
        return meta.getOwner();
    }

    @Nullable
    @Override
    public String getTexture(@NotNull Skull skull) {
        return getTexture(getProfile(skull));
    }

    @Nullable
    @Override
    public String getTexture(@NotNull SkullMeta meta) {
        return getTexture(getProfile(meta));
    }

    @Nullable
    @Override
    public String getTexture(@NotNull OfflinePlayer player) {
        return getTexture(getProfile(player));
    }

    private String getTexture(@Nullable GameProfile profile) {
        if (profile == null) return null;
        return profile.getProperties().get("textures").iterator().next().getValue();
    }

    @Override
    public void copyProfile(@NotNull Skull skull, @NotNull SkullMeta meta) {
        setProfile(skull, getProfile(meta));
    }

    @Override
    public void forceSetProfile(@NotNull SkullMeta meta, @NotNull String name) {
        GameProfile profile = new GameProfile(HPUtils.getUUID(name), name);
        setProfile(meta, profile);
    }

    @Override
    public void forceSetProfileTexture(@NotNull SkullMeta meta, @NotNull String name, @NotNull String texture) {
        GameProfile profile = getProfile(meta);
        if (profile == null) {
            profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), name);
        }
        profile.getProperties().put("textures", new Property("texture", texture));
        setProfile(meta, profile);
    }

    private static GameProfile getProfile(Object profileObj) {
        try {
            Field field = profileObj.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            return (GameProfile) field.get(profileObj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T setProfile(T obj, GameProfile profile) {
        try {
            Method profileMethod = obj.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            profileMethod.setAccessible(true);
            profileMethod.invoke(obj, profile);
        } catch (NoSuchMethodException e) {
            Field profileField;
            try {
                profileField = obj.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(obj, profile);
            } catch (NoSuchFieldException | IllegalAccessException noSuchFieldException) {
                noSuchFieldException.printStackTrace();
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
