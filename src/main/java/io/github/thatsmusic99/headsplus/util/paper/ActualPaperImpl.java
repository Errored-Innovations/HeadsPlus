package io.github.thatsmusic99.headsplus.util.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ActualPaperImpl implements PaperImpl {

    private static final Executor asyncExecutor = task -> Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.getInstance(), task);
    private static final Executor syncExecutor = task -> Bukkit.getScheduler().runTask(HeadsPlus.getInstance(), task);
    private Method createProfile;
    private Method setPlayerProfile;

    {
        try {
            createProfile = Server.class.getMethod("createProfile", UUID.class, String.class);
            setPlayerProfile = SkullMeta.class.getMethod("setPlayerProfile", PlayerProfile.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<SkullMeta> setProfile(SkullMeta meta, String name) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid;
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                uuid = player.getUniqueId();
            } else {
                uuid = UUID.nameUUIDFromBytes(name.getBytes());
            }

            try {
                PlayerProfile profile = (PlayerProfile) createProfile.invoke(Bukkit.getServer(), uuid, name); // use reflection because of the million APIs provided for Server
                profile.complete();
                setPlayerProfile.invoke(meta, profile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return meta;
        }, asyncExecutor).thenApplyAsync(sm -> sm, syncExecutor);
    }
}
