package io.github.thatsmusic99.headsplus.profile;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Targeted at Paper
 */
public class PaperProfileHandler implements IProfileHandler {

    @Override
    public boolean canUse() {

        try {
            Class.forName("com.destroystokyo.paper.profile.PlayerProfile");
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    @Nullable
    @Override
    public String getName(@NotNull Skull skull) {
        if (skull.getPlayerProfile() == null) return skull.getOwner();
        return skull.getPlayerProfile().getName();
    }

    @Nullable
    @Override
    public String getName(@NotNull SkullMeta meta) {
        if (meta.getPlayerProfile() == null) return meta.getOwner();
        return meta.getPlayerProfile().getName();
    }

    @Nullable
    @Override
    public String getTexture(@NotNull Skull skull) {
        return getTexture(skull.getPlayerProfile(), skull.getOwner());
    }

    @Nullable
    @Override
    public String getTexture(@NotNull SkullMeta meta) {
        return getTexture(meta.getPlayerProfile(), meta.getOwner());
    }

    @Nullable
    @Override
    public String getTexture(@NotNull OfflinePlayer player) {
        return getTexture(player.getPlayerProfile(), player.getName());
    }

    private String getTexture(PlayerProfile profile, String otherwise) {
        if (profile == null) return null;
        if (!profile.hasTextures()) return null;
        for (ProfileProperty property : profile.getProperties()) {
            if (!property.getName().equals("textures")) continue;
            return property.getValue();
        }
        return otherwise;
    }

    @Override
    public void copyProfile(@NotNull Skull skull, @NotNull SkullMeta meta) {
        meta.setPlayerProfile(skull.getPlayerProfile());
    }

    @Override
    public void forceSetProfile(@NotNull SkullMeta meta, @NotNull String name) {
        PlayerProfile profile = Bukkit.getServer().createProfile(HPUtils.getUUID(name), name);
        profile.complete(true, true);
        meta.setPlayerProfile(profile);
    }

    @Override
    public void forceSetProfileTexture(@NotNull SkullMeta meta, @NotNull String texture) {
        PlayerProfile profile = Bukkit.getServer().createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
        profile.setProperty(new ProfileProperty("textures", texture));
        profile.complete(true, true);
        meta.setPlayerProfile(profile);
    }
}
