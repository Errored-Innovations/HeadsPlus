package io.github.thatsmusic99.headsplus.util.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ActualPaperImpl implements PaperImpl {

    private static final Executor asyncExecutor = task -> Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.getInstance(), task);
    private static final Executor syncExecutor = task -> Bukkit.getScheduler().runTask(HeadsPlus.getInstance(), task);

    @Override
    public CompletableFuture<SkullMeta> setProfile(SkullMeta meta, String name) {
        return CompletableFuture.supplyAsync(() -> {
            UUID uuid;
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
            if (player.getUniqueId().equals(offlineUUID)) {
                String uuidStr = HeadsPlus.getInstance().getHeadsXConfig().grabUUID(name, 0, null);
                if (uuidStr == null) {
                    uuid = offlineUUID;
                } else {
                    uuid = UUID.fromString(uuidStr.substring(0, 8) +
                            "-" + uuidStr.substring(8, 12) +
                            "-" + uuidStr.substring(12, 16) +
                            "-" + uuidStr.substring(16, 20) +
                            "-" + uuidStr.substring(20));
                }
            } else {
                uuid = player.getUniqueId();
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
}
