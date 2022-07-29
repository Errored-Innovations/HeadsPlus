package io.github.thatsmusic99.headsplus.util.paper;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.managers.AutograbManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import scala.concurrent.impl.FutureConvertersImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ActualPaperImpl implements PaperImpl {

    private static final Executor asyncExecutor = task -> Bukkit.getScheduler().runTaskAsynchronously(HeadsPlus.get()
            , task);
    private static final Executor syncExecutor = task -> Bukkit.getScheduler().runTask(HeadsPlus.get(), task);
    private static final HashMap<String, String> LEGACY_TO_MINIMESSAGE = new HashMap<>();

    public ActualPaperImpl() {
        LEGACY_TO_MINIMESSAGE.put("§0", "<black>");
        LEGACY_TO_MINIMESSAGE.put("§1", "<dark_blue>");
        LEGACY_TO_MINIMESSAGE.put("§2", "<dark_green>");
        LEGACY_TO_MINIMESSAGE.put("§3", "<dark_aqua>");
        LEGACY_TO_MINIMESSAGE.put("§4", "<dark_red>");
        LEGACY_TO_MINIMESSAGE.put("§5", "<dark_purple>");
        LEGACY_TO_MINIMESSAGE.put("§6", "<gold>");
        LEGACY_TO_MINIMESSAGE.put("§7", "<gray>");
        LEGACY_TO_MINIMESSAGE.put("§8", "<dark_gray>");
        LEGACY_TO_MINIMESSAGE.put("§9", "<blue>");
        LEGACY_TO_MINIMESSAGE.put("§a", "<green>");
        LEGACY_TO_MINIMESSAGE.put("§b", "<aqua>");
        LEGACY_TO_MINIMESSAGE.put("§c", "<red>");
        LEGACY_TO_MINIMESSAGE.put("§d", "<light_purple>");
        LEGACY_TO_MINIMESSAGE.put("§e", "<yellow>");
        LEGACY_TO_MINIMESSAGE.put("§f", "<white>");
        LEGACY_TO_MINIMESSAGE.put("§l", "<b>");
        LEGACY_TO_MINIMESSAGE.put("§o", "<i>");
        LEGACY_TO_MINIMESSAGE.put("§m", "<st>");
        LEGACY_TO_MINIMESSAGE.put("§n", "<u>");
        LEGACY_TO_MINIMESSAGE.put("§k", "<obf>");
    }

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
        sender.sendMessage(MiniMessage.miniMessage().deserialize(replaceLegacy(message)));
    }

    @Override
    public void setDisplayName(ItemMeta meta, String name) {
        if (name == null) {
            meta.displayName(null);
            return;
        }
        meta.displayName(MiniMessage.miniMessage().deserialize(replaceLegacy(name)).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public void setLore(ItemMeta meta, List<String> lore) {
        List<Component> components = new ArrayList<>();
        for (String loreStr : lore) {
            if (loreStr == null) {
                components.add(null);
                return;
            }
            components.add(MiniMessage.miniMessage().deserialize(replaceLegacy(loreStr)).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(components);
    }

    private String replaceLegacy(String str) {
        if (str == null) return null;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != '§') continue;
            if (i + 1 == str.length()) continue;
            char code = str.charAt(i + 1);
            String result = c + "" + code;
            if (!LEGACY_TO_MINIMESSAGE.containsKey(result)) continue;
            int end = Math.min((i + 2), str.length());
            str = str.substring(0, i) + LEGACY_TO_MINIMESSAGE.get(result) + str.substring(end);
        }
        return str;
    }
}
