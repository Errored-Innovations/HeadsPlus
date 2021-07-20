package io.github.thatsmusic99.headsplus.util.paper;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import io.papermc.lib.PaperLib;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PaperUtil implements PaperImpl {

    private final PaperImpl internalImpl;
    private static PaperUtil instance;

    public PaperUtil() {
        PaperImpl impl;
        instance = this;
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
            if (profile == null) return;
            profile.getProperties().put("textures", new Property("texture", texture));
            ProfileFetcher.setProfile(meta, profile);
            return;
        }
        internalImpl.forceSetProfileTexture(meta, texture);
    }

    public static PaperUtil get() {
        return instance;
    }
}
