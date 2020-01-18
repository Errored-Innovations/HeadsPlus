package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

@CommandInfo(
        commandname = "complete",
        permission = "headsplus.maincommand.complete",
        subcommand = "complete",
        usage = "/hp complete <Challenge name> [Player]",
        maincommand = true)
public class Complete implements IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        Player p = sender instanceof Player ? (Player) sender : null;
        try {
            if (args.length > 1) {
                Challenge c = HeadsPlus.getInstance().getChallengeByName(args[1]);
                if (c != null) {
                    if (args.length > 2) {
                        if (sender.hasPermission("headsplus.maincommand.complete.others")) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
                            if (player.isOnline()) {
                                if (!c.isComplete(player.getPlayer())) {
                                    if (c.canComplete(player.getPlayer())) {
                                        return "";
                                    } else {
                                        return hpc.getString("commands.challenges.cant-complete-challenge", p);
                                    }
                                } else {
                                    return hpc.getString("commands.challenges.already-complete-challenge", p);
                                }

                            } else {
                                return hpc.getString("commands.errors.player-offline", p);
                            }
                        } else {
                            return hpc.getString("commands.errors.no-perm", p);
                        }

                    } else if (sender instanceof Player) {
                        if (!c.isComplete(p)) {
                            if (c.canComplete(p)) {
                                return "";
                            } else {
                                return hpc.getString("commands.challenges.cant-complete-challenge", p);
                            }
                        } else {
                            return hpc.getString("commands.challenges.already-complete-challenge", p);
                        }

                    } else {
                        return ChatColor.RED + "You must be a player to use this command!";
                    }
                } else {
                    return hpc.getString("commands.challenges.no-such-challenge", p);
                }
            } else {
                return hpc.getString("commands.errors.invalid-args", p);
            }
        } catch (SQLException e) {
            DebugPrint.createReport(e, "Complete command (checks)", true, sender);
            return hpc.getString("commands.errors.cmd-fail", p);
        }

    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.complete", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args.length > 2) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[2]);
            HeadsPlus.getInstance().getChallengeByName(args[1]).complete(player.getPlayer());
        } else {
            HeadsPlus.getInstance().getChallengeByName(args[1]).complete((Player) sender);
        }
        return false;
    }
}
