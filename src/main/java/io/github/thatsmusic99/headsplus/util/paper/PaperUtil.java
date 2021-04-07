package io.github.thatsmusic99.headsplus.util.paper;

import io.github.thatsmusic99.headsplus.HeadsPlus;
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
                HeadsPlus.getInstance().getLogger().log(Level.WARNING, "Failed to initialize Paper integration", e);
            }
        } else {
            impl = null;
        }
        internalImpl = impl;
    }

    public CompletableFuture<SkullMeta> setProfile(SkullMeta meta, String name) {
        if (internalImpl == null) {
            return CompletableFuture.completedFuture(HeadsPlus.getInstance().getNMS().setSkullOwner(name, meta));
        }
        return internalImpl.setProfile(meta, name);
    }

    public static PaperUtil get() {
        return instance;
    }
}
