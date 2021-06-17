package io.github.thatsmusic99.headsplus.util.paper;

import com.mojang.authlib.GameProfile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class PaperUtil implements PaperImpl {

    private final PaperImpl internalImpl;

    public PaperUtil() {
        PaperImpl impl;
        if (PaperLib.isPaper()) {
            try {
                impl = new ActualPaperImpl();
            } catch (Exception e) {
                impl = null;
                HeadsPlus.getInstance().getLogger().log(Level.WARNING, "Failed to initialize Paper integration", e);
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

}
