package io.github.thatsmusic99.headsplus.util.paper;

import org.bukkit.inventory.meta.SkullMeta;

import java.util.concurrent.CompletableFuture;

public interface PaperImpl {
    CompletableFuture<SkullMeta> setProfile(SkullMeta meta, String name);

    CompletableFuture<SkullMeta> setProfileTexture(SkullMeta meta, String texture);
}
