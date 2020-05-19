package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.util.CachedValues;
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

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.xp", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        HPPlayer player = HPPlayer.getHPPlayer(Bukkit.getOfflinePlayer(args[1]));
        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("add")) {
                if (args.length > 3) {
                    if (CachedValues.MATCH_PAGE.matcher(args[3]).matches()) {
                        int amount = Integer.parseInt(args[3]);
                        player.addXp(amount);
                        sender.sendMessage(hpc.getString("commands.xp.added-xp", sender).replaceAll("\\{player}", args[1])
                                .replaceAll("\\{xp}", String.valueOf(player.getXp()))
                                .replaceAll("\\{amount}", String.valueOf(args[3])));
                        return true;
                    } else {
                        sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                    }
                } else {
                    hpc.sendMessage("commands.errors.invalid-args", sender);
                }

            } else if (args[2].equalsIgnoreCase("subtract")) {
                if (args.length > 3) {
                    if (CachedValues.MATCH_PAGE.matcher(args[3]).matches()) {
                        int amount = Integer.parseInt(args[3]);
                        if (amount > player.getXp() && !HeadsPlus.getInstance().getConfiguration().getPerks().negative_xp) {
                            sender.sendMessage(hpc.getString("commands.xp.negative-xp", sender));
                            return true;
                        }
                        player.removeXp(amount);
                        sender.sendMessage(hpc.getString("commands.xp.removed-xp", sender).replaceAll("\\{player}", args[1])
                                .replaceAll("\\{xp}", String.valueOf(player.getXp()))
                                .replaceAll("\\{amount}", String.valueOf(args[3])));
                    } else {
                        sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
                    }
                } else {
                    hpc.sendMessage("commands.errors.invalid-args", sender);
                }
            } else if (args[2].equalsIgnoreCase("reset")) {
                player.setXp(0);
                hpc.sendMessage("commands.xp.reset-xp", sender, "{player}", args[1]);
            } else {
                hpc.sendMessage("commands.xp.current-xp", sender, "{player}", args[1], "{xp}", String.valueOf(player.getXp()));
            }
        } else {
            hpc.sendMessage("commands.xp.current-xp", sender, "{player}", args[1], "{xp}", String.valueOf(player.getXp()));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(), results);
        } else if (args.length == 3) {
            StringUtil.copyPartialMatches(args[2], Arrays.asList("view", "add", "subtract", "reset"), results);
        }
        return results;
    }
}
