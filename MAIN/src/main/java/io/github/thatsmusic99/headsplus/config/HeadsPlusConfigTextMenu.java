package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.HeadsPlusAPI;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.util.DataManager;
import io.github.thatsmusic99.headsplus.util.PagedHashmaps;
import io.github.thatsmusic99.headsplus.util.PagedLists;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.*;

public class HeadsPlusConfigTextMenu extends ConfigSettings {

    private static HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();


    public HeadsPlusConfigTextMenu() {
        this.conName = "textmenus";
        enable(false);
    }

    @Override
    protected void load(boolean nullp) {
        getConfig().addDefault("default-header", "&c・．&7━━━━━━━━━━━━ &8❰ &c&lHeadsPlus &8❱ &7━━━━━━━━━━━━&c．・");
        getConfig().addDefault("default-header-paged", "&c・．&7━━━━━━━━━━━━ &8❰ &c&lHeadsPlus &7{page}/{pages} &8❱ &7━━━━━━━━━━━━&c．・");
        getConfig().addDefault("help.header", "{default-paged}");
        getConfig().addDefault("help.for-each-line", "&c{usage} &8» &7{description}");
        getConfig().addDefault("help.lines-per-page", 8);
        getConfig().addDefault("help.command-help.header", "{default}");
        getConfig().addDefault("help.command-help.layout", new ArrayList<>(Arrays.asList("{header}",
                "&c{msg_textmenus.help.usage} &8» &7{usage}",
                "&c{msg_textmenus.help.description} &8» &7{description}",
                "&c{msg_textmenus.help.permission} &8» &7{permission}",
                "&c{msg_textmenus.help.further-usages} &8» &7{further-usage}")));
        getConfig().addDefault("head-info.header", "{default}");
        getConfig().addDefault("head-info.normal-layout", new ArrayList<>(Arrays.asList("{header}",
                "&c{msg_textmenus.head-info.type} &8» &7{type}",
                "&c{msg_textmenus.head-info.display-name} &8» &7{display-name}",
                "&c{msg_textmenus.head-info.price} &8» &7{price}",
                "&c{msg_textmenus.head-info.interact-name} &8» &7{interact-name}",
                "&c{msg_textmenus.head-info.chance} &8» &7{chance}")));
        getConfig().addDefault("head-info.mask-info.header", "{default}");
        getConfig().addDefault("head-info.mask-info.first-line", "&c{msg_textmenus.head-info.type} &8» &7{type}");
        getConfig().addDefault("head-info.mask-info.for-each-line", "&c{effect} &8» &7({amplifier})");
        getConfig().addDefault("head-info.mask-info.lines-per-page", 8);
        getConfig().addDefault("head-info.lore-info.header", "{default}");
        getConfig().addDefault("head-info.lore-info.first-line", "&c{msg_textmenus.head-info.type} &8» &7{type}");
        getConfig().addDefault("head-info.lore-info.for-each-line", "&8» &7{lore}");
        getConfig().addDefault("head-info.lore-info.lines-per-page", 8);
        getConfig().addDefault("head-info.name-info.colored.header", "{default}");
        getConfig().addDefault("head-info.name-info.colored.first-line", "&c{msg_textmenus.head-info.type} &8» &7{type}");
        getConfig().addDefault("head-info.name-info.colored.for-each-line", "&c{name} &8» &7({color})");
        getConfig().addDefault("head-info.name-info.colored.lines-per-page", 8);
        getConfig().addDefault("head-info.name-info.default.header", "{default}");
        getConfig().addDefault("head-info.name-info.default.first-line", "&c{msg_textmenus.head-info.type} &8» &7{type}");
        getConfig().addDefault("head-info.name-info.default.for-each-line", "&8» &7{name}");
        getConfig().addDefault("head-info.name-info.default.lines-per-page", 8);
        getConfig().addDefault("profile.header", "{default}");
        getConfig().addDefault("profile.layout", new ArrayList<>(Arrays.asList("{header}",
                "&c{msg_textmenus.profile.player} &8» &7{player}",
                "&cXP &8» &7{xp}",
                "&c{msg_textmenus.profile.completed-challenges} &8» &7{completed-challenges}",
                "&c{msg_textmenus.profile.total-heads-dropped} &8» &7{hunter-counter}",
                "&c{msg_textmenus.profile.total-heads-sold} &8» &7{sellhead-counter}",
                "&c{msg_textmenus.profile.total-heads-crafted} &8» &7{crafting-counter}",
                "&c{msg_textmenus.profile.current-level} &8» &7{level}",
                "&c{msg_textmenus.profile.xp-until-next-level} &8» &7{next-level}")));
        getConfig().addDefault("blacklist.default.header", "&c・．&7━━━━━━━━━━━━ &8❰ &c&lBlacklist &7{page}/{pages} &8❱ &7━━━━━━━━━━━━&c．・");
        getConfig().addDefault("blacklist.default.for-each-line", "&8» &7{name}");
        getConfig().addDefault("blacklist.default.lines-per-page", 8);
        getConfig().addDefault("blacklist.world.header", "&c・．&7━━━━━━━━━━━━ &8❰ &c&lWorld Blacklist &7{page}/{pages} &8❱ &7━━━━━━━━━━━━&c．・");
        getConfig().addDefault("blacklist.world.for-each-line", "&8» &7{name}");
        getConfig().addDefault("blacklist.world.lines-per-page", 8);
        getConfig().addDefault("whitelist.default.header", "&c・．&7━━━━━━━━━━━━ &8❰ &c&lWhitelist: &7{page}/{pages} &8❱ &7━━━━━━━━━━━━&c．・");
        getConfig().addDefault("whitelist.default.for-each-line", "&8» &7{name}");
        getConfig().addDefault("whitelist.default.lines-per-page", 8);
        getConfig().addDefault("whitelist.world.header", "&c・．&7━━━━━━━━━━━━ &8❰ &c&lWorld Whitelist: &7{page}/{pages} &8❱ &7━━━━━━━━━━━━&c．・");
        getConfig().addDefault("whitelist.world.for-each-line", "&8» &7{name}");
        getConfig().addDefault("whitelist.world.lines-per-page", 8);
        getConfig().addDefault("leaderboard.header", "&c・．&7━━━━━ &8❰ &c&lHeadsPlus Leaderboards: {section} &7{page}/{pages} &8❱ &7━━━━━&c．・");
        getConfig().addDefault("leaderboard.for-each-line", "&7{pos} &8» &c{name} &8⟶ &7{score}");
        getConfig().addDefault("leaderboard.lines-per-page", 8);
        getConfig().addDefault("info.header", "{default}");
        getConfig().addDefault("info.layout", new ArrayList<>(Arrays.asList("{header}",
                "&c{msg_textmenus.info.version} &8» &7{version}",
                "&c{msg_textmenus.info.author} &8» &7{author}",
                "&c(msg_textmenus.info.language} &8» &7{locale}",
                "&c{msg_textmenus.info.contributors} &8» &7{contributors}",
                "&c{msg_textmenus.info.spigot} &8» &7https://www.spigotmc.org/resources/headsplus-1-8-x-1-15-x.40265/",
                "&c{msg_textmenus.info.discord} &8» &7https://discord.gg/nbT7wC2",
                "&c{msg_textmenus.info.github} &8» &7https://github.com/Thatsmusic99/HeadsPlus")));
        getConfig().options().copyDefaults(true);
        save();
    }

    private static String translateColors(String s, CommandSender sender) {
        HeadsPlus hp = HeadsPlus.getInstance();
        return ChatColor.translateAlternateColorCodes('&', hpc.formatMsg(translateHeader(s).replaceAll("\\{1}", hp.getThemeColour(1).toString())
                .replaceAll("\\{2}", hp.getThemeColour(2).toString())
                .replaceAll("\\{3}", hp.getThemeColour(3).toString())
                .replaceAll("\\{4}", hp.getThemeColour(4).toString()), sender));
    }


    private static String translateHeader(String s) {
        return s.replaceAll("\\{default}", HeadsPlus.getInstance().getMenus().getConfig().getString("default-header"))
                .replaceAll("\\{default-paged}", HeadsPlus.getInstance().getMenus().getConfig().getString("default-header-paged"));
    }

    public static class BlacklistTranslator {

        public static String translate(CommandSender sender, String type, String type2, List<String> l, int page) {

            StringBuilder sb = new StringBuilder();
            HeadsPlusConfigTextMenu h = HeadsPlus.getInstance().getMenus();
            PagedLists<String> list = new PagedLists<>(l, h.getConfig().getInt(type + "." + type2 + ".lines-per-page"));
            if ((page > list.getTotalPages()) || (0 >= page)) {
                return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-pg-no", sender);
            }
            sb.append(translateColors(h.getConfig().getString(type + "." + type2 + ".header")
                    .replaceAll("\\{page}", String.valueOf(page))
            .replaceAll("\\{pages}", String.valueOf(list.getTotalPages())), sender)).append("\n");
            for (String str : list.getContentsInPage(page)) {
                sb.append(translateColors(str.replaceAll("\\{name}", str), sender)).append("\n");
            }
            return sb.toString();
        }
    }

    public static class ProfileTranslator {
        public static String translate(HPPlayer p, CommandSender sender) throws SQLException {
            StringBuilder sb = new StringBuilder();
            HeadsPlusConfigTextMenu h = HeadsPlus.getInstance().getMenus();
            HeadsPlusAPI api = HeadsPlus.getInstance().getAPI();
            for (String str : h.getConfig().getStringList("profile.layout")) {
                try {
                    String stri = translateColors(str.replace("{player}", p.getPlayer().getName())
                            .replaceAll("\\{xp}", String.valueOf(p.getXp()))
                            .replaceAll("\\{completed-challenges}", String.valueOf(p.getCompleteChallenges().size()))
                            .replaceAll("\\{hunter-counter}", String.valueOf(api.getPlayerInLeaderboards(p.getPlayer(), "total", "headspluslb", true)))
                            .replaceAll("\\{sellhead-counter}", String.valueOf(api.getPlayerInLeaderboards(p.getPlayer(), "total", "headsplussh", true)))
                            .replaceAll("\\{crafting-counter}", String.valueOf(api.getPlayerInLeaderboards(p.getPlayer(), "total", "headspluscraft", true)))
                            .replace("{header}", h.getConfig().getString("profile.header")), sender);
                    if (!(stri.contains("{level}") || (stri.contains("{next-level}")))) {
                        sb.append(stri).append("\n");
                    } else {
                        stri = stri.replaceAll("\\{level}", ChatColor.translateAlternateColorCodes('&', p.getLevel().getDisplayName()))
                                .replaceAll("\\{next-level}", String.valueOf((p.getNextLevel() != null ? (p.getNextLevel().getRequiredXP() - p.getXp()) : 0)));
                        sb.append(stri).append("\n");
                    }

                } catch (NullPointerException ignored) {

                }
            }
            return sb.toString();
        }
    }

    public static class HeadInfoTranslator {

        private static class Head {
            String type;
            String colour;

            Head(String t, String c) {
                type = t;
                colour = c;
            }
        }

        private static class Mask {
            String type;
            int amplifier;
            String effect;

            Mask(String t, int a, String e) {
                type = t;
                amplifier = a;
                effect = e;
            }
        }

        public static String translateNormal(String type, CommandSender sender) {
            StringBuilder sb = new StringBuilder();
            HeadsPlusConfigTextMenu h = HeadsPlus.getInstance().getMenus();
            HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
            for (String str : h.getConfig().getStringList("head-info.normal-layout")) {
                sb.append(translateColors(str.replaceAll("\\{header}", h.getConfig().getString("head-info.header"))
                .replace("{type}", type)
                .replace("{display-name}", hpch.getDisplayName(type))
                .replaceAll("\\{price}", String.valueOf(hpch.getPrice(type)))
                .replaceAll("\\{interact-name}", hpch.getInteractName(type))
                .replaceAll("\\{chance}", String.valueOf(hpch.getConfig().getDouble(type + ".chance"))), sender)).append("\n");
            }
            return sb.toString();
        }

        public static String translateColored(CommandSender sender, String type, int page) {
            StringBuilder sb = new StringBuilder();
            HeadsPlusConfigTextMenu ht = HeadsPlus.getInstance().getMenus();
            HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
            List<Head> h = new ArrayList<>();
            for (String t : hpch.getConfig().getConfigurationSection(type + ".name").getKeys(false)) {
                for (String r : hpch.getConfig().getStringList(type + ".name." + t)) {
                    h.add(new Head(r, t));
                }
            }
            PagedLists<Head> hs = new PagedLists<>(h, ht.getConfig().getInt("head-info.name-info.colored.lines-per-page"));
            if ((page > hs.getTotalPages()) || (0 >= page)) {
                return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-pg-no", sender);
            }
            sb.append(translateColors(ht.getConfig().getString("head-info.name-info.colored.header"), sender)).append("\n");
            sb.append(translateColors(ht.getConfig().getString("head-info.name-info.colored.first-line"), sender)
            .replace("{type}", type));
            for (Head head : hs.getContentsInPage(page)) {
                sb.append("\n").append(translateColors(ht.getConfig().getString("head-info.name-info.colored.for-each-line"), sender)
                .replace("{name}", head.type)
                .replaceAll("\\{color}", head.colour));
            }
            return sb.toString();
        }

        public static String translateMaskInfo(CommandSender sender, String type, int page) {
            StringBuilder sb = new StringBuilder();
            HeadsPlusConfigTextMenu ht = HeadsPlus.getInstance().getMenus();
            HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
            List<Mask> m = new ArrayList<>();
            for (int i = 0; i < hpch.getConfig().getStringList(type + ".mask-effects").size(); i++) {
                String s = hpch.getConfig().getStringList(type + ".mask-effects").get(i);
                int a = 1;
                try {
                    a = hpch.getConfig().getIntegerList(type + ".mask-amplifiers").get(i) + 1;
                } catch (IndexOutOfBoundsException ignored) {
                }
                m.add(new Mask(type, a, s));

            }
            PagedLists<Mask> hs = new PagedLists<>(m, ht.getConfig().getInt("head-info.mask-info.lines-per-page"));
            if ((page > hs.getTotalPages()) || (0 >= page)) {
                return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-pg-no", sender);
            }
            sb.append(translateColors(ht.getConfig().getString("head-info.mask-info.header"), sender)).append("\n");
            sb.append(translateColors(ht.getConfig().getString("head-info.mask-info.first-line"), sender)
                    .replaceAll("\\{type}", type));
            for (Mask mask : hs.getContentsInPage(page)) {
                sb.append("\n").append(sb.append(translateColors(ht.getConfig().getString("head-info.mask-info.for-each-line"), sender)
                        .replaceAll("\\{effect}", mask.effect)
                        .replaceAll("\\{amplifier}", String.valueOf(mask.amplifier))));
            }
            return sb.toString();
        }

        public static String translateLoreInfo(CommandSender sender, String type, int page) {
            StringBuilder sb = new StringBuilder();
            HeadsPlusConfigTextMenu ht = HeadsPlus.getInstance().getMenus();
            HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();
            PagedLists<String> lore = new PagedLists<>(hpch.getConfig().getStringList(type + ".lore"), ht.getConfig().getInt("head-info.lore-info.lines-per-page"));
            if ((page > lore.getTotalPages()) || (0 >= page)) {
                return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.invalid-pg-no", sender);
            }
            sb.append(translateColors(ht.getConfig().getString("head-info.lore-info.header"), sender)).append("\n");
            sb.append(translateColors(ht.getConfig().getString("head-info.lore-info.first-line"), sender)
                    .replaceAll("\\{type}", type));
            for (String s : lore.getContentsInPage(page)) {
                sb.append("\n").append(translateColors(ht.getConfig().getString("head-info.lore-info.for-each-line")
                .replace("{lore}", s), sender));
            }
            return sb.toString();
        }
    }

    public static class HelpMenuTranslator {

        public static void translateHelpMenu(CommandSender sender, int page) {
            HeadsPlus hp = HeadsPlus.getInstance();
            HeadsPlusConfigTextMenu ht = HeadsPlus.getInstance().getMenus();
            List<IHeadsPlusCommand> headPerms = new ArrayList<>();
            for (IHeadsPlusCommand key : hp.getCommands()) {
                CommandInfo c = key.getClass().getAnnotation(CommandInfo.class);
                if (sender.hasPermission(c.permission())) {
                    headPerms.add(key);
                }
            }
            PagedLists<IHeadsPlusCommand> pl = new PagedLists<>(headPerms, ht.getConfig().getInt("help.lines-per-page"));

            if ((page > pl.getTotalPages()) || (0 >= page)) {
                sender.sendMessage(hp.getMessagesConfig().getString("commands.errors.invalid-pg-no", sender));
            } else {
                sender.sendMessage(translateColors(ht.getConfig().getString("help.header"), sender).replaceAll("\\{page}", String.valueOf(page))
                        .replaceAll("\\{pages}", String.valueOf(pl.getTotalPages())));
                for (IHeadsPlusCommand key : pl.getContentsInPage(page)) {
                    CommandInfo c = key.getClass().getAnnotation(CommandInfo.class);
                    String help = translateColors(ht.getConfig().getString("help.for-each-line")
                            .replace("{usage}", c.usage())
                            .replace("{description}", key.getCmdDescription(sender)), sender);
                    TextComponent component = new TextComponent(help);
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/hp " + c.commandname()));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(translateCommandHelp(key, sender)).create()));
                    try {
                        sender.spigot().sendMessage(component);
                    } catch (Exception e) {
                        sender.sendMessage(help);
                    }
                }
            }
        }

        public static String translateCommandHelp(IHeadsPlusCommand key, CommandSender sender) {
            StringBuilder sb = new StringBuilder();
            HeadsPlusConfigTextMenu ht = HeadsPlus.getInstance().getMenus();
            for (String s : ht.getConfig().getStringList("help.command-help.layout")) {
                if (!s.contains("{permission}") || sender.hasPermission("headsplus.help.viewperms")) {
                    if (s.contains("{further-usage}") && key.advancedUsages().length > 0) {
                        sb.append(translateColors(s.replaceAll("\\{further-usage}", ""), sender));
                        for (String s2 : key.advancedUsages()) {
                            sb.append("\n").append(HeadsPlus.getInstance().getThemeColour(4)).append(translateColors(s2, sender));
                        }
                    } else if (!s.contains("{further-usage}")){
                        CommandInfo c = key.getClass().getAnnotation(CommandInfo.class);
                        sb.append("\n").append(translateColors(s.replaceAll("\\{header}", ht.getConfig().getString("help.command-help.header"))
                                .replace("{description}", key.getCmdDescription(sender)).replaceAll("\\{usage}", c.usage()), sender)
                                .replaceAll("\\{permission}", c.permission()));
                    }
                }
            }
            return sb.toString();
        }
    }

    public static class LeaderBoardTranslator {

        public static String translate(CommandSender sender, String section, String database, int page) {
            PagedHashmaps<OfflinePlayer, Integer> ph = null;
            HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();
            try {
                HeadsPlus hp = HeadsPlus.getInstance();
                StringBuilder sb = new StringBuilder();
                HeadsPlusConfigTextMenu ht = hp.getMenus();
                ph = new PagedHashmaps<>(DataManager.getScores(database, section, false), ht.getConfig().getInt("leaderboard.lines-per-page"));
                sb.append(translateColors(ht.getConfig().getString("leaderboard.header")
                        .replace("{section}", WordUtils.capitalize(section))
                        .replaceAll("\\{page}", String.valueOf(page))
                        .replaceAll("\\{pages}", String.valueOf(ph.getTotalPages())), sender));
                Set<OfflinePlayer> it = ph.getContentsInPage(page).keySet();
                Collection<Integer> it2 = ph.getContentsInPage(page).values();
                for (int i = 0; i < it.size(); i++) {
                    try {
                        int in = i + (ph.getContentsPerPage() * (ph.getCurrentPage() - 1));
                        sb.append("\n").append(translateColors(ht.getConfig().getString("leaderboard.for-each-line")
                                .replaceAll("\\{pos}", String.valueOf(in + 1))
                                .replace("{name}", ((OfflinePlayer)it.toArray()[i]).getName())
                                .replaceAll("\\{score}", String.valueOf(it2.toArray()[i])), sender));
                    } catch (NullPointerException ignored) {
                    }
                }
                return sb.toString();
            } catch (IllegalArgumentException ex) {
                if (ph.getHs().size() > 0) {
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
            StringBuilder sb = new StringBuilder();
            HeadsPlusConfigTextMenu h = HeadsPlus.getInstance().getMenus();
            HeadsPlus hp = HeadsPlus.getInstance();
            for (String s : h.getConfig().getStringList("info.layout")) {
                sb.append("\n").append(translateColors(s
                        .replaceAll("\\{version}", String.valueOf(hp.getVersion()))
                        .replace("{header}", h.getConfig().getString("info.header"))
                        .replace("{author}", String.valueOf(hp.getAuthor()))
                        .replace("{locale}", hp.getConfiguration().getConfig().getString("locale"))
                        .replaceAll("\\{contributors}", "Toldi, DariusTK, AlansS53, Gneiwny, steve4744, Niestrat99, Alexisparis007, jascotty2, Gurbiel, Mistermychciak, stashenko/The_stas, YouHaveTrouble"), sender));
            }
            return sb.toString();
        }
    }
}
