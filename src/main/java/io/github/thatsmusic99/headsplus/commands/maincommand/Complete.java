package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "complete",
        permission = "headsplus.maincommand.complete",
        subcommand = "complete",
        usage = "/hp complete <Challenge name> [Player]",
        maincommand = true)
public class Complete implements IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.complete", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            if (args.length > 1) {
                Challenge c = HeadsPlus.get().getChallengeByName(args[1]);
                if (c != null) {
                    if (args.length > 2) {
                        if (sender.hasPermission("headsplus.maincommand.complete.others")) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                            if (player.isOnline()) {
                                if (!c.isComplete(player.getPlayer())) {
                                    if (c.canComplete(player.getPlayer())) {
                                        HeadsPlus.get().getChallengeByName(args[1]).complete(player.getPlayer());
                                    } else {
                                        hpc.sendMessage("commands.challenges.cant-complete-challenge", sender);
                                    }
                                } else {
                                    hpc.sendMessage("commands.challenges.already-complete-challenge", sender);
                                }
                            } else {
                                hpc.sendMessage("commands.errors.player-offline", sender);
                                return false;
                            }
                        } else {
                            hpc.sendMessage("commands.errors.no-perm", sender);
                        }
                        return true;

                    } else if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (!c.isComplete(p)) {
                            if (c.canComplete(p)) {
                                HeadsPlus.get().getChallengeByName(args[1]).complete((Player) sender);
                            } else {
                                hpc.sendMessage("commands.challenges.cant-complete-challenge", sender);
                            }
                        } else {
                            hpc.sendMessage("commands.challenges.already-complete-challenge", sender);
                        }

                    } else {
                        hpc.sendMessage("commands.errors.not-a-player", sender);
                    }
                    return true;
                } else {
                    hpc.sendMessage("commands.challenges.no-such-challenge", sender);
                }
            } else {
                hpc.sendMessage("commands.errors.invalid-args", sender);
            }
        } catch (SQLException e) {
            DebugPrint.createReport(e, "Complete command (checks)", true, sender);
            hpc.sendMessage("commands.errors.cmd-fail", sender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            List<String> challenges = new ArrayList<>();
            for (Challenge challenge : HeadsPlus.get().getChallenges()) {
                challenges.add(challenge.getConfigName());
            }
            StringUtil.copyPartialMatches(args[1], challenges, results);
        } else if (args.length == 3 && sender.hasPermission("headsplus.maincommand.complete.others")) {
            StringUtil.copyPartialMatches(args[2], IHeadsPlusCommand.getPlayers(sender), results);
        }
        return results;
    }
}
