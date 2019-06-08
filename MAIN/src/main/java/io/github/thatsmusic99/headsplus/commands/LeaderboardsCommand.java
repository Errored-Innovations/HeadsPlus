package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.sql.SQLException;
import java.util.HashMap;

@CommandInfo(
        commandname = "hplb",
        permission = "headsplus.leaderboards",
        subcommand = "Hplb",
        maincommand = false,
        usage = "/hplb [Total|Entity|Page No.] [Page No.] [Hunting|Selling|Crafting]"
)
public class LeaderboardsCommand implements CommandExecutor, IHeadsPlusCommand {

    private final HashMap<String, Boolean> tests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender cs, Command command, String s, String[] args) {
        try {
            tests.put("No permission", !cs.hasPermission("headsplus.leaderboards"));
            tests.put("Arguments", args.length > 0);
            if (cs.hasPermission("headsplus.leaderboards")) {
                if (args.length > 0) {
                    try {
                        boolean b = HeadsPlus.getInstance().getDeathEvents().ableEntities.contains(EntityType.valueOf(args[0].toUpperCase()));
                        tests.put("Valid Entity", b);
                        String sec = b ? args[0].toUpperCase() : args[0];
                        if (b || sec.equalsIgnoreCase("player")) {
                            if (args.length > 1) {
                                if (args[1].matches("^[0-9]+$")) {
                                    if (args.length > 2) {
                                        if (args[2].equalsIgnoreCase("crafting")
                                                || args[2].equalsIgnoreCase("selling")
                                                || args[2].equalsIgnoreCase("hunting")) {
                                            cs.sendMessage(getLeaderboard(sec, Integer.parseInt(args[1]), args[2]));
                                        } else {
                                            cs.sendMessage(getLeaderboard(sec, Integer.parseInt(args[1]), "hunting"));
                                        }
                                    } else {
                                        cs.sendMessage(getLeaderboard(sec, Integer.parseInt(args[1]), "hunting"));
                                    }
                                } else if (args[1].equalsIgnoreCase("crafting")
                                        || args[1].equalsIgnoreCase("selling")
                                        || args[1].equalsIgnoreCase("hunting")) {
                                    if (args.length > 2) {
                                        if (args[2].matches("^[0-9]+$")) {
                                            cs.sendMessage(getLeaderboard(sec, Integer.parseInt(args[2]), args[1]));
                                        } else {
                                            cs.sendMessage(getLeaderboard(sec, 1, args[1]));
                                        }
                                    } else {
                                        cs.sendMessage(getLeaderboard(sec, 1, args[1]));
                                    }
                                } else {
                                    cs.sendMessage(getLeaderboard(sec, 1, "hunting"));
                                }
                            } else {
                                cs.sendMessage(getLeaderboard(sec, 1, "hunting"));
                            }
                            printDebugResults(tests, true);
                            return true;
                        }
                    } catch (IllegalArgumentException ex) {
                        tests.put("Valid Entity", false);
                        if (args[0].equalsIgnoreCase("total")) {
                            if (args.length > 1) {
                                if (args[1].matches("^[0-9]+$")) {
                                    if (args.length > 2) {
                                        if (args[2].equalsIgnoreCase("crafting")
                                                || args[2].equalsIgnoreCase("selling")
                                                || args[2].equalsIgnoreCase("hunting")) {
                                            cs.sendMessage(getLeaderboard(args[0], Integer.parseInt(args[1]), args[2]));
                                        } else {
                                            cs.sendMessage(getLeaderboard(args[0], Integer.parseInt(args[1]), "hunting"));
                                        }
                                    } else {
                                        cs.sendMessage(getLeaderboard(args[0], Integer.parseInt(args[1]), "hunting"));
                                    }
                                } else if (args[1].equalsIgnoreCase("crafting")
                                        || args[1].equalsIgnoreCase("selling")
                                        || args[1].equalsIgnoreCase("hunting")) {
                                    cs.sendMessage(getLeaderboard(args[0], 1, args[1]));
                                } else {
                                    cs.sendMessage(getLeaderboard(args[0], 1, "hunting"));
                                }
                            } else {
                                cs.sendMessage(getLeaderboard(args[0], 1, "hunting"));
                            }
                            printDebugResults(tests, true);
                            return true;
                        } else if (args[0].matches("^[0-9]+$")) {
                            cs.sendMessage(getLeaderboard("total", Integer.parseInt(args[0]), "hunting"));
                            printDebugResults(tests, true);
                            return true;
                        } else if (args[0].equalsIgnoreCase("player")) {
                            if (args.length > 1) {
                                if (args[1].matches("^[0-9]+$")) {
                                    if (args.length > 2) {
                                        if (args[2].equalsIgnoreCase("crafting")
                                                || args[2].equalsIgnoreCase("selling")
                                                || args[2].equalsIgnoreCase("hunting")) {
                                            cs.sendMessage(getLeaderboard(args[0], Integer.parseInt(args[1]), args[2]));
                                        } else {
                                            cs.sendMessage(getLeaderboard(args[0], Integer.parseInt(args[1]), "hunting"));
                                        }
                                    } else {
                                        cs.sendMessage(getLeaderboard(args[0], Integer.parseInt(args[1]), "hunting"));
                                    }
                                } else {
                                    if (args.length > 2) {
                                        if (args[2].equalsIgnoreCase("crafting")
                                                || args[2].equalsIgnoreCase("selling")
                                                || args[2].equalsIgnoreCase("hunting")) {
                                            cs.sendMessage(getLeaderboard(args[0], 1, args[2]));
                                        } else {
                                            cs.sendMessage(getLeaderboard(args[0], 1, "hunting"));
                                        }
                                    } else {
                                        cs.sendMessage(getLeaderboard(args[0], 1, "hunting"));
                                    }
                                }

                            } else {
                                cs.sendMessage(getLeaderboard(args[0], 1, "hunting"));
                            }
                            printDebugResults(tests, true);
                            return true;
                        }  else if (args[0].equalsIgnoreCase("crafting")
                                || args[0].equalsIgnoreCase("selling")
                                || args[0].equalsIgnoreCase("hunting")) {
                            if (args.length > 1) {
                                if (args[1].matches("^[0-9]+$")) {
                                    cs.sendMessage(getLeaderboard("total", Integer.parseInt(args[1]), args[0]));
                                } else {
                                    cs.sendMessage(getLeaderboard("total", 1, args[0]));
                                }
                            }


                        } else {
                            cs.sendMessage(getLeaderboard("total", 1, "hunting"));
                        }
                    }

                } else {
                    cs.sendMessage(getLeaderboard("total", 1, "hunting"));
                    printDebugResults(tests, true);
                    return true;
                }
            }
        } catch (Exception e) {
            new DebugPrint(e, "Command (leaderboard)", true, cs);
        }
        printDebugResults(tests, false);
        return false;
    }

    private String getLeaderboard(String sec, int page, String part) throws SQLException {
        return HeadsPlusConfigTextMenu.LeaderBoardTranslator.translate(sec, part, page);
    }

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descHPLeaderboards();
    }

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        h.put(true, "");
        return h;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        return false;
    }
}
