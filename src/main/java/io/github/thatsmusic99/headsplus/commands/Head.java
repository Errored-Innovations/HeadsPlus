package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.managers.RestrictionsManager;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@CommandInfo(
        commandname = "head",
        permission = "headsplus.head",
        maincommand = false,
        usage = "/head <IGN> [Player]",
        descriptionPath = "descriptions.head")
public class Head implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final MessagesManager hpc = MessagesManager.get();

    private void giveHead(Player p, String n) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);

        final Player player = Bukkit.getPlayer(n);
        final CompletableFuture<SkullMeta> result;

        if (player != null) {
            final String texture = HeadsPlus.get().getProfileHandler().getTexture(player);
            result = texture == null ?
                    HeadsPlus.get().getProfileHandler().setProfile((SkullMeta) skull.getItemMeta(), n) :
                    HeadsPlus.get().getProfileHandler().setProfileTexture((SkullMeta) skull.getItemMeta(), n, texture);
        } else {
            result = HeadsPlus.get().getProfileHandler().setProfile((SkullMeta) skull.getItemMeta(), n);
        }

        result.whenCompleteAsync((meta, err) -> {

            if (err != null) {
                hpc.sendMessage("commands.errors.cmd-fail", p);
                err.printStackTrace();
                return;
            }

            meta.setDisplayName(ConfigMobs.get().getPlayerDisplayName(n));
            skull.setItemMeta(meta);
            p.getInventory().addItem(skull);
        }, HeadsPlus.sync);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                             @NotNull String[] args) {
        try {
            if (!sender.hasPermission("headsplus.head")) {
                hpc.sendMessage("commands.errors.no-perm", sender);
                return true;
            }
            if (args.length > 1) {
                if (!sender.hasPermission("headsplus.head.others")) {
                    hpc.sendMessage("commands.errors.no-perm", sender);
                    return true;
                }
                // Check for player
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    hpc.sendMessage("commands.errors.player-offline", sender);
                    return true;
                }

                if (args[0].length() > 16) {
                    hpc.sendMessage("commands.head.head-too-long", sender);
                    return true;
                }

                // Check the player name specified
                if (!CachedValues.PLAYER_NAME.matcher(args[0]).matches()) {
                    hpc.sendMessage("commands.head.alpha-names", sender);
                    return true;
                }
                // Check restrictions
                if (!RestrictionsManager.canUse(args[0].toLowerCase(), RestrictionsManager.ActionType.HEADS)) {
                    hpc.sendMessage("commands.head.restricted-head", sender);
                    return true;
                }
                giveHead(target, args[0]);
                return true;
            } else if (args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    // Check the player name specified
                    if (!CachedValues.PLAYER_NAME.matcher(args[0]).matches()) {
                        hpc.sendMessage("commands.head.invalid-args", sender);
                        return true;
                    }
                    // Check restrictions
                    if (!RestrictionsManager.canUse(args[0].toLowerCase(), RestrictionsManager.ActionType.HEADS)) {
                        // TODO: cannot use head
                        return true;
                    }
                    giveHead(p, args[0]);
                } else {
                    hpc.sendMessage("commands.errors.not-a-player", sender);
                }
            } else {
                hpc.sendMessage("commands.errors.invalid-args", sender);
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Command (head)", true, sender);
        }
        return false;
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission("headsplus.head")) {
            StringUtil.copyPartialMatches(args[0], IHeadsPlusCommand.getPlayers(sender), results);
        } else if (args.length == 2 && sender.hasPermission("headsplus.head.others")) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(sender), results);
        }
        return results;
    }
}
	
