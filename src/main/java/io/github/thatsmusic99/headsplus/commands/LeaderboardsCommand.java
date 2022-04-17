package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigTextMenus;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.managers.SellableHeadsManager;
import io.github.thatsmusic99.headsplus.sql.StatisticsSQLManager;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(
        commandname = "hplb",
        permission = "headsplus.leaderboards",
        maincommand = false,
        usage = "/hplb [Hunting|Selling|Crafting|Page No.] [Mob|Page No.] [ID|Page No.] ",
        descriptionPath = "descriptions.hplb")
public class LeaderboardsCommand implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender cs, @NotNull Command command, @NotNull String s,
                             @NotNull String[] args) {
        if (!MainConfig.get().getMainFeatures().LEADERBOARDS) {
            return true;
        }
        if (!cs.hasPermission("headsplus.leaderboards")) {
            return true;
        }
        if (args.length == 0) {
            StatisticsSQLManager.get().getLeaderboardTotal().thenAccept(list ->
                    cs.sendMessage(ConfigTextMenus.LeaderBoardTranslator.translate(cs, "Total", list, 1)));
            return true;
        }
        // Check category
        StatisticsSQLManager.CollectionType type = StatisticsSQLManager.CollectionType.getType(args[0].toUpperCase());
        int page = 1;

        if (type == null) {
            StatisticsSQLManager.get().getLeaderboardTotal().thenAccept(list ->
                    cs.sendMessage(ConfigTextMenus.LeaderBoardTranslator.translate(cs, "Total", list, checkPage(args[0]))));
            return true;
        }

        if (args.length == 1) {
            StatisticsSQLManager.get().getLeaderboardTotal(type).thenAccept(list ->
                    cs.sendMessage(ConfigTextMenus.LeaderBoardTranslator.translate(cs,
                            HeadsPlus.capitalize(type.name()), list, page)));
            return true;
        }

        if (args.length == 2) {
            if (args[1].matches("^[0-9]+$")) {
                StatisticsSQLManager.get().getLeaderboardTotal(type).thenAccept(list ->
                        cs.sendMessage(ConfigTextMenus.LeaderBoardTranslator.translate(cs, "Total", list, checkPage(args[1]))));
            } else {
                String key = args[1].toUpperCase();
                if (args[0].equalsIgnoreCase("hunting")) key = "entity=" + key;
                StatisticsSQLManager.get().getLeaderboardTotalMetadata(type, key).thenAccept(list ->
                        cs.sendMessage(ConfigTextMenus.LeaderBoardTranslator.translate(cs, "Total", list, 1)));
            }
            return true;
        }

        if (args.length == 3) {
            String key = args[1].toUpperCase();
            if (args[0].equalsIgnoreCase("hunting")) key = "entity=" + key;
            if (args[2].matches("^[0-9]+$")) {
                StatisticsSQLManager.get().getLeaderboardTotalMetadata(type, key).thenAccept(list ->
                        cs.sendMessage(ConfigTextMenus.LeaderBoardTranslator.translate(cs, "Total", list, checkPage(args[2]))));
            } else {
                StatisticsSQLManager.get().getLeaderboardTotal(type, args[2], key).thenAccept(list ->
                        cs.sendMessage(ConfigTextMenus.LeaderBoardTranslator.translate(cs, "Total", list, 1)));
            }
            return true;
        }

        String key = args[1].toUpperCase();
        if (args[0].equalsIgnoreCase("hunting")) key = "entity=" + key;
        StatisticsSQLManager.get().getLeaderboardTotal(type, args[2], key).thenAccept(list ->
                cs.sendMessage(ConfigTextMenus.LeaderBoardTranslator.translate(cs, "Total", list, checkPage(args[3]))));

        return true;
    }

    private int checkPage(String input) {
        return CachedValues.MATCH_PAGE.matcher(input).matches() ? Integer.parseInt(input) : 1;

    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList("hunting", "crafting"), results);
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("hunting")) {
                StringUtil.copyPartialMatches(args[1], EntityDataManager.ableEntities, results);
            } else {
                StringUtil.copyPartialMatches(args[1], SellableHeadsManager.get().getKeys(), results);
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("hunting")) {
                List<EntityDataManager.DroppedHeadInfo> list = EntityDataManager.getStoredHeads().get(args[1].toUpperCase() + ";default");
                List<String> names = new ArrayList<>();
                list.forEach(str -> names.add(str.getId()));
                StringUtil.copyPartialMatches(args[2], names, results);
            }
        }
        return results;
    }
}
