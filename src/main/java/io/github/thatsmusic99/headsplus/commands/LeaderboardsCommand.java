package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.listeners.DeathEvents;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(
        commandname = "hplb",
        permission = "headsplus.leaderboards",
        subcommand = "Hplb",
        maincommand = false,
        usage = "/hplb [Hunting|Selling|Crafting|Page No.] [ID|Page No.] [Page No.] "
)
public class LeaderboardsCommand implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
            if (cs.hasPermission("headsplus.leaderboards")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {

                            if (args.length > 0) {
                                try {
                                    switch (args[0].toLowerCase()) {
                                        case "hunting":
                                        case "selling":
                                        case "crafting":
                                            if (args.length > 1) {
                                                if (SellHead.getRegisteredIDs().contains(args[1])
                                                        || args[1].equalsIgnoreCase("total")) {
                                                    if (args.length > 2) {
                                                        if (CachedValues.MATCH_PAGE.matcher(args[2]).matches()) {
                                                            try {
                                                                cs.sendMessage(getLeaderboard(cs, args[1], Integer.parseInt(args[2]), args[0].toLowerCase()));
                                                                return;
                                                            } catch (IllegalArgumentException ignored) {
                                                            }
                                                        }
                                                    }
                                                    cs.sendMessage(getLeaderboard(cs, args[1], 1, args[0].toLowerCase()));
                                                    return;
                                                }
                                                if (CachedValues.MATCH_PAGE.matcher(args[1]).matches()) {
                                                    cs.sendMessage(getLeaderboard(cs, "total", Integer.parseInt(args[1]), args[0].toLowerCase()));
                                                    return;
                                                }
                                            }
                                            cs.sendMessage(getLeaderboard(cs, "total", 1, args[0].toLowerCase()));
                                            return;
                                        default:
                                            if (CachedValues.MATCH_PAGE.matcher(args[0]).matches()) {
                                                cs.sendMessage(getLeaderboard(cs, "total", Integer.parseInt(args[0]), "hunting"));
                                                return;
                                            }
                                            cs.sendMessage(getLeaderboard(cs, "total", 1, "hunting"));
                                    }
                                } catch (Exception e) {
                                    DebugPrint.createReport(e, "Command (leaderboard)", true, cs);
                                }
                            } else {
                                cs.sendMessage(getLeaderboard(cs, "total", 1, "hunting"));
                            }
                        }

                    }.runTaskAsynchronously(HeadsPlus.getInstance());
                }
        return false;
    }

    private String getLeaderboard(CommandSender sender, String sec, int page, String part) {
        return HeadsPlusConfigTextMenu.LeaderBoardTranslator.translate(sender, sec, part, page);
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hplb", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList("hunting", "selling", "crafting"), results);
        } else if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], SellHead.getRegisteredIDs(), results);
        }
        return results;
    }
}
