package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(commandname = "xp", permission = "headsplus.maincommand.xp", subcommand = "XP", usage = "/hp xp <Player Name> [View|Add|Subtract|Reset] [Amount]", maincommand = true)
public class XPCommand implements IHeadsPlusCommand {

    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.xp", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args.length > 1) {
            HPPlayer player = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[1]));
            if (args.length > 2) {
                switch (args[2].toLowerCase()) {
                    case "add":
                        if (sender.hasPermission("headsplus.maincommand.xp.add")) {
                            if (args.length > 3) {
                                int amount = HPUtils.isInt(args[3]);
                                player.addXp(amount);
                                hpc.sendMessage("commands.xp.added-xp", sender, "{player}", args[1], "{xp}", String.valueOf(player.getXp()), "{amount}", args[3]);
                                return true;
                            } else {
                                hpc.sendMessage("commands.errors.invalid-args", sender);
                            }
                        } else {
                            hpc.sendMessage("commands.errors.no-perm", sender);
                        }
                        break;
                    case "subtract":
                        if (sender.hasPermission("headsplus.maincommand.xp.subtract")) {
                            if (args.length > 3) {
                                int amount = HPUtils.isInt(args[3]);
                                if (amount > player.getXp() && !MainConfig.get().getMiscellaneous().ALLOW_NEGATIVE_XP) {
                                    hpc.sendMessage("commands.xp.negative-xp", sender);
                                    return true;
                                }
                                player.removeXp(amount);
                                hpc.sendMessage("commands.xp.remove-xp", sender, "{player}", args[1], "{xp}", String.valueOf(player.getXp()), "{amount}", args[3]);
                            } else {
                                hpc.sendMessage("commands.errors.invalid-args", sender);
                            }
                        } else {
                            hpc.sendMessage("commands.errors.no-perm", sender);
                        }
                        break;
                    case "reset":
                        if (sender.hasPermission("headsplus.maincommand.reset")) {
                            player.setXp(0);
                            hpc.sendMessage("commands.xp.reset-xp", sender, "{player}", args[1]);
                        } else {
                            hpc.sendMessage("commands.errors.no-perm", sender);
                        }
                        break;
                    case "view":
                        if (sender.hasPermission("headsplus.maincommand.xp.view")) {
                            hpc.sendMessage("commands.xp.current-xp", sender, "{player}", args[1], "{xp}", String.valueOf(player.getXp()));
                        }
                        break;
                    default:
                        hpc.sendMessage("commands.errors.invalid-args", sender);
                        break;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(sender), results);
        } else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], Arrays.asList("view", "add", "subtract", "reset"), results);
        }
        return results;
    }
}
