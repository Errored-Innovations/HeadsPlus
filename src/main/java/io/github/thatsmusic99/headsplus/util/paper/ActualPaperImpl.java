package io.github.thatsmusic99.headsplus.util.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.AutograbManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ActualPaperImpl implements PaperImpl {

    private static final Executor asyncExecutor = task -> Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get()
            , task);
    private static final Executor syncExecutor = task -> Bukkit.getScheduler().runTask(HeadsPlus.get(), task);

    @Override
    public CompletableFuture<SkullMeta> setProfile(SkullMeta meta, String name) {
        return CompletableFuture.supplyAsync(() -> {
            forceSetProfile(meta, name);
            return meta;
        }, asyncExecutor).thenApplyAsync(sm -> sm, syncExecutor);
    }

    @Override
    public CompletableFuture<SkullMeta> setProfileTexture(SkullMeta meta, String texture) {
        return CompletableFuture.supplyAsync(() -> {
            forceSetProfileTexture(meta, texture);
            return meta;
        }, asyncExecutor).thenApplyAsync(sm -> sm, syncExecutor);
    }

    @Override
    public void forceSetProfile(SkullMeta meta, String name) {
        HeadsPlus.debug("Setting the head name using Paper.");
        UUID uuid;
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes());
        if (player.getUniqueId().equals(offlineUUID)) {
            String uuidStr = AutograbManager.grabUUID(name, 0, null);
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
            profile.complete(true, true);
            meta.setPlayerProfile(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forceSetProfileTexture(SkullMeta meta, String texture) {
        HeadsPlus.debug("Setting the head texture using Paper.");
        PlayerProfile profile = Bukkit.getServer().createProfile(UUID.nameUUIDFromBytes(texture.getBytes()), "HPXHead");
        profile.setProperty(new ProfileProperty("textures", texture));
        profile.complete(true, true);
        meta.setPlayerProfile(profile);
    }

    @Override
    public String getTexture(Player player) {
        PlayerProfile profile = player.getPlayerProfile();
        if (!profile.hasTextures()) return "";
        for (ProfileProperty property : profile.getProperties()) {
            if (!property.getName().equals("textures")) continue;
            return property.getValue();
        }
        return player.getName();
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }
}
