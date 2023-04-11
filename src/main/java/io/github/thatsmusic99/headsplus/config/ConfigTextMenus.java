package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Level;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.managers.LevelsManager;
import io.github.thatsmusic99.headsplus.sql.ChallengeSQLManager;
import io.github.thatsmusic99.headsplus.sql.PlayerSQLManager;
import io.github.thatsmusic99.headsplus.sql.StatisticsSQLManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.PagedLists;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ConfigTextMenus extends HPConfig {

    private static ConfigTextMenus instance;

    public ConfigTextMenus() throws IOException, IllegalAccessException {
        super("textmenus.yml");
        instance = this;
    }

    // TODO - make an interface that all translators override?

    @Override
    public void loadDefaults() {
        addDefault("default-header", "&c・．&7━━━━━━━━━━━━ &8❰ &c&lHeadsPlus &8❱ &7━━━━━━━━━━━━&c．・");
        addDefault("default-header-paged", "&c・．&7━━━━━━━━━━━━ &8❰ &c&lHeadsPlus &7{page}/{pages} &8❱ " +
                "&7━━━━━━━━━━━━&c．・");
        addDefault("help.header", "{default-paged}");
        addDefault("help.for-each-line", "&c{usage} &8» &7{description}");
        addDefault("help.lines-per-page", 8);
        addDefault("help.command-help.header", "{default}");
        addDefault("help.command-help.layout", new ArrayList<>(Arrays.asList("{header}",
                "&c{msg_textmenus.help.usage} &8» &7{usage}",
                "&c{msg_textmenus.help.description} &8» &7{description}",
                "&c{msg_textmenus.help.permission} &8» &7{permission}",
                "&c{msg_textmenus.help.further-usages} &8» &7{further-usage}")));
        addDefault("head-info.header", "{default}");
        addDefault("head-info.normal-layout", new ArrayList<>(Arrays.asList("{header}",
                "&c{msg_textmenus.head-info.type} &8» &7{type}",
                "&c{msg_textmenus.head-info.display-name} &8» &7{display-name}",
                "&c{msg_textmenus.head-info.price} &8» &7{price}",
                "&c{msg_textmenus.head-info.interact-name} &8» &7{interact-name}",
                "&c{msg_textmenus.head-info.chance} &8» &7{chance}")));
        addDefault("head-info.mask-info.header", "{default}");
        addDefault("head-info.mask-info.first-line", "&c{msg_textmenus.head-info.type} &8» &7{type}");
        addDefault("head-info.mask-info.for-each-line", "&c{effect} &8» &7({amplifier})");
        addDefault("head-info.mask-info.lines-per-page", 8);
        addDefault("head-info.lore-info.header", "{default}");
        addDefault("head-info.lore-info.first-line", "&c{msg_textmenus.head-info.type} &8» &7{type}");
        addDefault("head-info.lore-info.for-each-line", "&8» &7{lore}");
        addDefault("head-info.lore-info.lines-per-page", 8);
        addDefault("head-info.name-info.colored.header", "{default}");
        addDefault("head-info.name-info.colored.first-line", "&c{msg_textmenus.head-info.type} &8» &7{type}");
        addDefault("head-info.name-info.colored.for-each-line", "&c{name} &8» &7({color})");
        addDefault("head-info.name-info.colored.lines-per-page", 8);
        addDefault("head-info.name-info.default.header", "{default}");
        addDefault("head-info.name-info.default.first-line", "&c{msg_textmenus.head-info.type} &8» &7{type}");
        addDefault("head-info.name-info.default.for-each-line", "&8» &7{name}");
        addDefault("head-info.name-info.default.lines-per-page", 8);
        addDefault("profile.header", "{default}");
        addDefault("profile.layout", new ArrayList<>(Arrays.asList("{header}",
                "&c{msg_textmenus.profile.player} &8» &7{player}",
                "&cXP &8» &7{xp}",
                "&c{msg_textmenus.profile.completed-challenges} &8» &7{completed-challenges}",
                "&c{msg_textmenus.profile.total-heads-dropped} &8» &7{hunter-counter}",
                "&c{msg_textmenus.profile.total-heads-crafted} &8» &7{crafting-counter}",
                "&c{msg_textmenus.profile.current-level} &8» &7{level}",
                "&c{msg_textmenus.profile.xp-until-next-level} &8» &7{next-level}")));
        addDefault("leaderboard.header", "&c・．&7━━━━━ &8❰ &c&lHeadsPlus Leaderboards: {section} &7{page}/{pages} &8❱ " +
                "&7━━━━━&c．・");
        addDefault("leaderboard.for-each-line", "&7{pos} &8» &c{name} &8⟶ &7{score}");
        addDefault("leaderboard.lines-per-page", 8);
        addDefault("info.header", "{default}");
        addDefault("info.layout", new ArrayList<>(Arrays.asList("{header}",
                "&c{msg_textmenus.info.version} &8» &7{version}",
                "&c{msg_textmenus.info.author} &8» &7{author}",
                "&c{msg_textmenus.info.language} &8» &7{locale}",
                "&c{msg_textmenus.info.contributors} &8» &7{contributors}",
                "&c{msg_textmenus.info.spigot} &8» &7https://www.spigotmc.org/resources/headsplus-1-8-x-1-15-x.40265/",
                "&c{msg_textmenus.info.discord} &8» &7https://discord.gg/eu8h3BG",
                "&c{msg_textmenus.info.github} &8» &7https://github.com/Thatsmusic99/HeadsPlus")));
    }

    private static String translateColors(String s, CommandSender sender) {
        return ChatColor.translateAlternateColorCodes('&', MessagesManager.get().formatMsg(s, sender));
    }


    private static String translateHeader(String s) {
        return s.replaceAll("\\{default}", instance.getString("default-header"))
                .replaceAll("\\{default-paged}", instance.getString("default-header-paged"));
    }

    public static class ProfileTranslator {

        public static CompletableFuture<String> translate(OfflinePlayer player, CommandSender sender) {
            return CompletableFuture.supplyAsync(() -> {
                List<String> profile = new ArrayList<>();
                int levelPos = 0;
                try {
                    levelPos = PlayerSQLManager.get().getLevel(player.getName(), false).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                Level level = null;
                if (levelPos > -1 && levelPos < LevelsManager.get().getLevels().size())
                    level = LevelsManager.get().getLevel(levelPos);
                Level nextLevel = null;
                if (level != null) nextLevel = LevelsManager.get().getNextLevel(level.getConfigName());
                final long xp;
                long xp1;
                try {
                    xp1 = PlayerSQLManager.get().getXP(player.getName(), true).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    xp1 = -1;
                }
                xp = xp1;
                Level finalLevel = level;
                Level finalNextLevel = nextLevel;
                for (String str : instance.getStringList("profile.layout")) {
                    String s = str;
                    if (!s.equals("{header}")) s = translateColors(s, sender);
                    HPUtils.parseLorePlaceholders(profile, s,
                            new HPUtils.PlaceholderInfo("{player}", player.getName(), true),
                            new HPUtils.PlaceholderInfo("{xp}", xp, true),
                            new HPUtils.PlaceholderInfo("{completed-challenges}",
                                    () -> processStat(ChallengeSQLManager.get().getTotalChallengesComplete(player.getUniqueId(), false)),
                                    MainConfig.get().getMainFeatures().CHALLENGES),
                            new HPUtils.PlaceholderInfo("{hunter-counter}",
                                    () -> processStat(StatisticsSQLManager.get().getStat(player.getUniqueId(),
                                            StatisticsSQLManager.CollectionType.HUNTING, false)),
                                    MainConfig.get().getMainFeatures().LEADERBOARDS),
                            new HPUtils.PlaceholderInfo("{sellhead-counter}", 0, false),
                            new HPUtils.PlaceholderInfo("{crafting-counter}",
                                    () -> processStat(StatisticsSQLManager.get().getStat(player.getUniqueId(),
                                            StatisticsSQLManager.CollectionType.CRAFTING, false)),
                                    MainConfig.get().getMainFeatures().LEADERBOARDS),
                            new HPUtils.PlaceholderInfo("{header}", instance.getHeader("profile.header", sender), true),
                            new HPUtils.PlaceholderInfo("{level}", () -> translateColors(finalLevel.getDisplayName(),
                                    sender), level != null),
                            new HPUtils.PlaceholderInfo("{next-level}", () -> {
                                assert finalNextLevel != null;
                                return finalNextLevel.getRequiredXP() - xp;
                            }, finalNextLevel != null));
                }
                return String.join("\n", profile);
            }, HeadsPlus.async).exceptionally(ex -> {
                ex.printStackTrace();
                return "no";
            });
        }
    }

    private static int processStat(CompletableFuture<Integer> stat) {
        try {
            return stat.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static class HelpMenuTranslator {

        public static void translateHelpMenu(CommandSender sender, int page) {
            HeadsPlus hp = HeadsPlus.get();
            List<IHeadsPlusCommand> headPerms = new ArrayList<>();
            for (IHeadsPlusCommand key : hp.getCommands().values()) {
                CommandInfo c = key.getClass().getAnnotation(CommandInfo.class);
                if (sender.hasPermission(c.permission())) {
                    headPerms.add(key);
                }
            }
            PagedLists<IHeadsPlusCommand> pl = new PagedLists<>(headPerms, instance.getInteger("help.lines-per-page"));

            if ((page > pl.getTotalPages()) || (0 >= page)) {
                MessagesManager.get().sendMessage("commands.errors.invalid-pg-no", sender);
            } else {
                sender.sendMessage(translateColors(instance.getString("help.header"), sender).replaceAll("\\{page}",
                                String.valueOf(page))
                        .replaceAll("\\{pages}", String.valueOf(pl.getTotalPages())));
                for (IHeadsPlusCommand key : pl.getContentsInPage(page)) {
                    CommandInfo c = key.getClass().getAnnotation(CommandInfo.class);
                    String help = translateColors(instance.getString("help.for-each-line")
                            .replace("{usage}", c.usage())
                            .replace("{description}", key.getCmdDescription(sender)), sender);
                    TextComponent component = new TextComponent(help);
                    // TODO - adventure time, one day...!
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            "/hp " + c.commandname()));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(translateCommandHelp(key, sender)).create()));
                    try {
                        sender.spigot().sendMessage(component);
                    } catch (Exception | NoSuchMethodError e) {
                        sender.sendMessage(help);
                    }
                }
            }
        }

        public static String translateCommandHelp(IHeadsPlusCommand key, CommandSender sender) {
            List<String> helpList = new ArrayList<>();
            CommandInfo c = key.getClass().getAnnotation(CommandInfo.class);
            for (String s : instance.getStringList("help.command-help.layout")) {
                HPUtils.parseLorePlaceholders(helpList, translateColors(s, sender),
                        new HPUtils.PlaceholderInfo("{permission}", c.permission(), sender.hasPermission("headsplus" +
                                ".help.viewperms")),
                        new HPUtils.PlaceholderInfo("{further-usage}", () -> {
                            List<String> strings = new ArrayList<>();
                            strings.add(translateColors(s.replaceAll("\\{further-usage}", ""), sender));
                            for (String str : key.advancedUsages()) {
                                strings.add(translateColors(str, sender));
                            }
                            return strings;
                        }, key.advancedUsages().length > 0),
                        new HPUtils.PlaceholderInfo("{header}", translateColors(instance.getString("help.command-help" +
                                ".header"), sender), true),
                        new HPUtils.PlaceholderInfo("{description}", key.getCmdDescription(sender), true),
                        new HPUtils.PlaceholderInfo("{usage}", c.usage(), true));
            }
            return String.join("\n", helpList);
        }
    }

    public static class LeaderBoardTranslator {

        public static String translate(CommandSender sender, String section,
                                       List<StatisticsSQLManager.LeaderboardEntry> entries, int page) {
            PagedLists<StatisticsSQLManager.LeaderboardEntry> ph;
            MessagesManager hpc = MessagesManager.get();
            try {
                StringBuilder sb = new StringBuilder();
                ph = new PagedLists<>(entries, instance.getInteger("leaderboard.lines-per-page"));
                sb.append(translateColors(instance.getString("leaderboard.header")
                        .replace("{section}", HeadsPlus.capitalize(section))
                        .replaceAll("\\{page}", String.valueOf(page))
                        .replaceAll("\\{pages}", String.valueOf(ph.getTotalPages())), sender));
                int index = ph.getContentsPerPage() * (page - 1);
                for (StatisticsSQLManager.LeaderboardEntry entry : ph.getContentsInPage(page)) {
                    try {
                        sb.append("\n").append(translateColors(instance.getString("leaderboard.for-each-line")
                                .replaceAll("\\{pos}", String.valueOf(++index))
                                .replace("{name}", entry.getPlayer())
                                .replaceAll("\\{score}", String.valueOf(entry.getSum())), sender));
                    } catch (NullPointerException ignored) {
                    }
                }
                return sb.toString();
            } catch (IllegalArgumentException ex) {
                if (entries.size() > 0) {
                    return hpc.getString("commands.errors.invalid-pg-no", sender);
                } else {
                    return hpc.getString("commands.errors.no-data-lb", sender);
                }
            } catch (NullPointerException ex) {
                return hpc.getString("commands.errors.no-data-lb", sender);
            }
        }
    }

    public static class InfoTranslator {

        public static String translate(CommandSender sender) {
            HeadsPlus hp = HeadsPlus.get();
            List<String> infoCommand = new ArrayList<>();
            for (String s : instance.getStringList("info.layout")) {
                String str = s;
                if (!str.equals("{header}")) str = translateColors(s, sender);
                HPUtils.parseLorePlaceholders(infoCommand, str,
                        new HPUtils.PlaceholderInfo("{version}", hp.getVersion(), true),
                        new HPUtils.PlaceholderInfo("{header}", () -> instance.getHeader("info.header", sender), true),
                        new HPUtils.PlaceholderInfo("{author}", hp.getAuthor(), true),
                        new HPUtils.PlaceholderInfo("{locale}", MainConfig.get().getLocalisation().LOCALE, true),
                        new HPUtils.PlaceholderInfo("{contributors}", "Toldi, DariusTK, AlansS53, Gneiwny, steve4744," +
                                " Niestrat99, Alexisparis007, jascotty2, Gurbiel, Mistermychciak, stashenko/The_stas," +
                                " YouHaveTrouble, Tepoloco, Bieck_Smile, PaulBGD, andy3559167", true));
            }
            return String.join("\n", infoCommand);
        }
    }

    private String getHeader(String path, CommandSender sender) {
        return translateColors(translateHeader(getString(path)), sender);
    }
}
