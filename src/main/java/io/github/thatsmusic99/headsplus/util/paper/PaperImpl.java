package io.github.thatsmusic99.headsplus.util.paper;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PaperImpl {
    CompletableFuture<SkullMeta> setProfile(SkullMeta meta, String name);

    CompletableFuture<SkullMeta> setProfileTexture(SkullMeta meta, String texture);

    void forceSetProfile(SkullMeta meta, String name);

    void forceSetProfileTexture(SkullMeta meta, String texture);

    String getTexture(Player player);

    void sendMessage(CommandSender sender, String message);

    void setDisplayName(ItemMeta meta, String name);

    void setLore(ItemMeta meta, List<String> lore);
}
