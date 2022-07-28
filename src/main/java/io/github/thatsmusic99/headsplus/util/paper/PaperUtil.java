package io.github.thatsmusic99.headsplus.util.paper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import io.papermc.lib.PaperLib;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PaperUtil implements PaperImpl {

    private final PaperImpl internalImpl;
    private static PaperUtil instance;
    private boolean adventureEnabled;

    public PaperUtil() {
        instance = this;
        PaperImpl impl;
        if (PaperLib.isPaper()) {
            try {
                impl = new ActualPaperImpl();
            } catch (Exception e) {
                impl = null;
                HeadsPlus.get().getLogger().log(Level.WARNING, "Failed to initialize Paper integration", e);
            }
        } else {
            impl = null;
        }

        try {
            Class.forName("net.kyori.adventure.text.Component");
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            adventureEnabled = true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            HeadsPlus.get().getLogger().warning("Adventure support not possible, failed to find class " + e.getMessage());
        }
        internalImpl = impl;
    }

    public CompletableFuture<SkullMeta> setProfile(SkullMeta meta, String name) {
        if (internalImpl == null) {
            return CompletableFuture.completedFuture(ProfileFetcher.setProfile(meta, name));
        }
        return internalImpl.setProfile(meta, name);
    }

    public CompletableFuture<SkullMeta> setProfileTexture(SkullMeta meta, String texture) {
        if (internalImpl == null) {
            forceSetProfileTexture(meta, texture);
            return CompletableFuture.completedFuture(meta);
        }
        return internalImpl.setProfileTexture(meta, texture);
    }

    @Override
    public void forceSetProfile(SkullMeta meta, String name) {
        if (internalImpl == null) {
            ProfileFetcher.setProfile(meta, name);
            return;
        }
        internalImpl.forceSetProfile(meta, name);
    }

    @Override
    public void forceSetProfileTexture(SkullMeta meta, String texture) {
        if (internalImpl == null) {
            GameProfile profile;
            try {
                profile = ProfileFetcher.getProfile(meta);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return;
            }
            if (profile == null) {
                profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
            }
            profile.getProperties().put("textures", new Property("texture", texture));
            ProfileFetcher.setProfile(meta, profile);
            return;
        }
        internalImpl.forceSetProfileTexture(meta, texture);
    }

    @Override
    public String getTexture(Player player) {
        return internalImpl == null ? player.getName() : internalImpl.getTexture(player);
    }

    public static PaperUtil get() {
        return instance;
    }

    public boolean useAdventure() {
        return adventureEnabled;
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        if (!adventureEnabled) return;
        internalImpl.sendMessage(sender, message);
    }
}
