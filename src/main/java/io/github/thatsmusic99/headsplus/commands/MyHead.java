package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.RestrictionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@CommandInfo(
        commandname = "myhead",
        permission = "headsplus.myhead",
        maincommand = false,
        usage = "/myhead",
        descriptionPath = "descriptions.myhead")
public class MyHead implements CommandExecutor, IHeadsPlusCommand {

    private final MessagesManager hpc = MessagesManager.get();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String l,
                             @NotNull String[] args) {
        try {
            if (sender.hasPermission("headsplus.myhead")) {
                if (!(sender instanceof Player)) {
                    hpc.sendMessage("commands.errors.not-a-player", sender);
                    return false;
                }
                Player p = (Player) sender;
                if (!RestrictionsManager.canUse(p.getName(), RestrictionsManager.ActionType.HEADS)) {
                    hpc.sendMessage("commands.head.restricted-head", sender);
                    return true;
                }
                giveHead(p, sender.getName());
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (myhead)", true, sender);
        }

        return false;
    }

    private void giveHead(Player p, String n) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        final Player player = Bukkit.getPlayer(n);
        final CompletableFuture<SkullMeta> result;

        if (player != null) {
            final String texture = HeadsPlus.get().getProfileHandler().getTexture(player);
            result = texture == null ?
                    HeadsPlus.get().getProfileHandler().setProfile((SkullMeta) skull.getItemMeta(), n) :
                    HeadsPlus.get().getProfileHandler().setProfileTexture((SkullMeta) skull.getItemMeta(), texture);
        } else {
            result = HeadsPlus.get().getProfileHandler().setProfile((SkullMeta) skull.getItemMeta(), n);
        }

        result.thenAcceptAsync(meta -> {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigMobs.get().getPlayerDisplayName(n)));
            skull.setItemMeta(meta);
            p.getInventory().addItem(skull);
        }, HeadsPlus.sync);
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        return new ArrayList<>();
    }
}
