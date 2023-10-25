package io.github.thatsmusic99.headsplus.profile;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;

public interface IProfileHandler {

    boolean canUse();

    @Nullable String getName(@NotNull Skull skull);

    @Nullable String getName(@NotNull SkullMeta meta);

    /**
     * Returns the base64 encoded texture of a skull block.
     *
     * @param skull the skull in question.
     * @return a base64 encoded texture, otherwise null
     */
    @Nullable String getTexture(@NotNull Skull skull);

    @Nullable String getTexture(@NotNull SkullMeta meta);

    @Nullable String getTexture(@NotNull OfflinePlayer player);

    void copyProfile(@NotNull Skull skull, @NotNull SkullMeta meta);

    void forceSetProfile(@NotNull SkullMeta meta, @NotNull String name);

    void forceSetProfileTexture(@NotNull SkullMeta meta, @NotNull String texture) throws MalformedURLException;

    default CompletableFuture<SkullMeta> setProfile(@NotNull SkullMeta meta, @NotNull String name) {
        return CompletableFuture.supplyAsync(() -> {
            forceSetProfile(meta, name);
            return meta;
        }, HeadsPlus.async);
    }

    default CompletableFuture<SkullMeta> setProfileTexture(@NotNull SkullMeta meta, @NotNull String texture) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                forceSetProfileTexture(meta, texture);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            return meta;
        }, HeadsPlus.async);
    }


}
