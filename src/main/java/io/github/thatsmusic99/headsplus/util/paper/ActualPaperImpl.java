package io.github.thatsmusic99.headsplus.util.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ActualPaperImpl implements PaperImpl {

    private static final Executor asyncExecutor = task -> Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get(), task);
    private static final Executor syncExecutor = task -> Bukkit.getScheduler().runTask(HeadsPlus.get(), task);

    @Override
    public CompletableFuture<SkullMeta> setProfile(SkullMeta meta, String name) {
        return CompletableFuture.supplyAsync(() -> {
            HeadsPlus.debug("Setting the head name using Paper. Also, you're stupid lmfao");
            UUID uuid;
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                uuid = player.getUniqueId();
            } else {
                uuid = UUID.nameUUIDFromBytes(name.getBytes());
            }

            try {
                PlayerProfile profile = Bukkit.getServer().createProfile(uuid, name);
                profile.complete();
                meta.setPlayerProfile(profile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return meta;
        }, asyncExecutor).thenApplyAsync(sm -> sm, syncExecutor);
    }

    @Override
    public CompletableFuture<SkullMeta> setProfileTexture(SkullMeta meta, String texture) {
        return CompletableFuture.supplyAsync(() -> {
            HeadsPlus.debug("Setting the head texture using Paper.");
            PlayerProfile profile = Bukkit.getServer().createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
            profile.setProperty(new ProfileProperty("textures", texture));
            profile.complete();
            meta.setPlayerProfile(profile);
            return meta;
        }, asyncExecutor).thenApplyAsync(sm -> sm, syncExecutor);
    }
}
