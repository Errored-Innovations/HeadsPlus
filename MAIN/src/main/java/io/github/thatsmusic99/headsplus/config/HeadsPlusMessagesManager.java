package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.util.DebugFileCreator;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeadsPlusMessagesManager {

    private static YamlConfiguration config;
    private static HashMap<String, YamlConfiguration> locales;
    private static HashMap<Player, YamlConfiguration> players;

	public HeadsPlusMessagesManager() {
	    HeadsPlus hp = HeadsPlus.getInstance();
	    HeadsPlusMainConfig mainConfig = hp.getConfiguration();
	    String locale = mainConfig.getConfig().getString("locale");
	    if (mainConfig.getConfig().getBoolean("smart-locale")) {
	    	locales = new HashMap<>();
	    	File langDir = new File(hp.getDataFolder() + File.separator + "locale" + File.separator);
	    	for (File f : langDir.listFiles()) {
	    	    locales.put(f.getName().split("_")[0].toLowerCase(), YamlConfiguration.loadConfiguration(f));
            }
	    	players = new HashMap<>();
	    }
	    // Main config for non-player entities such as console
	    try {
	        config = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, locale + ".yml"));
        } catch (Exception e) {
	        hp.getLogger().info("Failed to load the locale settings! This is caused by an invalid name provided. Setting locale to en_us...");
            config = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "en_us.yml"));
        }
	    if (config.getDouble("version") != 1.1) {
			new BukkitRunnable() {
				@Override
				public void run() {
					hp.getLogger().info("Locale configs are outdated! Updating messages...");
					YamlConfiguration en_us = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "en_us.yml"));
					YamlConfiguration de_de = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "de_de.yml"));
					YamlConfiguration es_es = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "es_es.yml"));
					YamlConfiguration fr_fr = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "fr_fr.yml"));
					YamlConfiguration hu_hu = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "hu_hu.yml"));
					YamlConfiguration lol_us = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "lol_us.yml"));
					YamlConfiguration pl_pl = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "pl_pl.yml"));
					YamlConfiguration ro_ro = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "ro_ro.yml"));
					YamlConfiguration ru_ru = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "ru_ru.yml"));

					en_us.addDefault("language", "English (US)");
					en_us.addDefault("commands.locale.invalid-lang", "{header} That is not a valid language!");
					en_us.addDefault("commands.locale.changed-locale", "{header} Good day! Your language is now set to &cEnglish (US)");
					en_us.addDefault("commands.locale.changed-locale-other", "{header} &c{player}&7's language is now set to &c{language}&7!");
					en_us.addDefault("inventory.icon.challenge.reward", "&6Reward: &a{reward}");
					en_us.addDefault("inventory.icon.challenge.xp", "&6XP: &a{xp}");
					en_us.addDefault("inventory.icon.challenge.count", "&7{challenge-count} challenges");
					en_us.addDefault("inventory.icon.close", "&8❰ &c&lClose Menu &8❱");
					en_us.addDefault("inventory.icon.favourites", "&8❰ &b&lFavorites &8❱");
					en_us.addDefault("inventory.icon.head.price", "&cPrice &8❱ &7{price}");
					en_us.addDefault("inventory.icon.head.favourite", "&cFavorite!");
					en_us.addDefault("inventory.icon.head.count", "&7{head-count} heads");
					en_us.addDefault("inventory.icon.menu", "&8❰ &a&lMain Menu &8❱");
					en_us.addDefault("inventory.icon.start", "&8❰ &a&lFirst Page &8❱");
					en_us.addDefault("inventory.icon.last", "&8❰ &a&lLast Page &8❱");
					en_us.addDefault("inventory.icon.back", "&8❰ &a&lBack &8❱");
					en_us.addDefault("inventory.icon.back-2", "&8❰ &a&lBack 2 &8❱");
					en_us.addDefault("inventory.icon.back-3", "&8❰ &a&lBack 3 &8❱");
					en_us.addDefault("inventory.icon.search", "&8❰ &e&lSearch Heads &8❱");
					en_us.addDefault("inventory.icon.stats.icon", "&8❰ &a&lStats &8❱");
					en_us.addDefault("inventory.icon.stats.total-heads", "&aTotal Heads &8❱ &e");
					en_us.addDefault("inventory.icon.stats.total-pages", "&aTotal Pages &8❱ &e");
					en_us.addDefault("inventory.icon.stats.total-sections", "&aTotal Sections &8❱ &e");
					en_us.addDefault("inventory.icon.stats.current-balance", "&aCurrent Balance &8❱ &e");
					en_us.addDefault("inventory.icon.stats.current-section", "&aCurrent Section &8❱ &e");
					en_us.addDefault("textmenus.profile.player", "Player");
					en_us.addDefault("textmenus.profile.completed-challenges", "Completed Challenges");
					en_us.addDefault("textmenus.profile.total-heads-dropped", "Total Heads Dropped");
					en_us.addDefault("textmenus.profile.total-heads-sold", "Total Heads Sold");
					en_us.addDefault("textmenus.profile.total-heads-crafted", "Total Heads Crafted");
					en_us.addDefault("textmenus.profile.current-level", "Current Level");
					en_us.addDefault("textmenus.profile.xp-until-next-level", "XP until next level");
					en_us.addDefault("textmenus.blacklist", "Blacklist");
					en_us.addDefault("textmenus.whitelist", "Whitelist");
					en_us.addDefault("textmenus.blacklistw", "World Blacklist");
					en_us.addDefault("textmenus.whitelistw", "World Whitelist");
					en_us.addDefault("textmenus.info.version", "Version");
					en_us.addDefault("textmenus.info.author", "Author");
					en_us.addDefault("textmenus.info.language", "Language");
					en_us.addDefault("textmenus.info.contributors", "Contributors");
					en_us.addDefault("textmenus.head-info.type", "Type");
					en_us.addDefault("textmenus.head-info.display-name", "Display Name");
					en_us.addDefault("textmenus.head-info.price", "Price");
					en_us.addDefault("textmenus.head-info.interact-name", "Interact Name");
					en_us.addDefault("textmenus.head-info.chance", "Chance");
					en_us.addDefault("textmenus.help.usage", "Usage");
					en_us.addDefault("textmenus.help.description", "Description");
					en_us.addDefault("textmenus.help.permission", "Permission");
					en_us.addDefault("textmenus.help.further-usages", "Further Usages");

					de_de.addDefault("language", "Deutsch (DE)");
					de_de.addDefault("commands.locale.invalid-lang", "");
					de_de.addDefault("commands.locale.changed-locale", "");

                    es_es.addDefault("language", "Español (ES)");
                    es_es.addDefault("commands.locale.invalid-lang", "");
                    es_es.addDefault("commands.locale.changed-locale", "");

                    fr_fr.addDefault("language", "Français (FR)");
                    fr_fr.addDefault("commands.locale.invalid-lang", "");
                    fr_fr.addDefault("commands.locale.changed-locale", "");

                    hu_hu.addDefault("language", "Magyar (MA)");
                    hu_hu.addDefault("commands.locale.invalid-lang", "");
                    hu_hu.addDefault("commands.locale.changed-locale", "");

                    lol_us.addDefault("language", "LOLCAT (Kingdom of Cats)");
                    lol_us.addDefault("commands.locale.invalid-lang", "");
				}
			}.runTaskAsynchronously(hp);
		}
    }

	public String getString(String path) {
	    String str = config.getString(path);
	    if (str == null) return "";
	    Pattern pat = Pattern.compile("\\{msg_(.+)}");
	    Matcher m = pat.matcher(str);
	    str = str.replaceAll("\\{header}", config.getString("prefix"));
	    str = str.replaceAll("''", "'");
	    str = str.replaceAll("^'", "");
	    str = str.replaceAll("'$", "");
	    while (m.find()) {
	        String s = m.group();
            str = str.replaceAll("\\{msg_" + s + "}", getString(s));
        }

	    str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }

    public String getString(String path, CommandSender cs) {
		return cs instanceof Player ? getString(path, (Player) cs) : getString(path);
	}

    public String getString(String path, Player player) {
	    if (player == null) return getString(path);
	    YamlConfiguration config = HeadsPlusMessagesManager.config;
	    if (HeadsPlus.getInstance().getConfiguration().getConfig().getBoolean("smart-locale")) {
	    	if (players.containsKey(player)) {
	    		config = players.get(player);
			}
		}
		String str = config.getString(path);
		if (str == null) return "";
		Pattern pat = Pattern.compile("\\{msg_(.+)}");
		Matcher m = pat.matcher(str);
		str = str.replaceAll("\\{header}", config.getString("prefix"));
		str = str.replaceAll("''", "'");
		str = str.replaceAll("^'", "");
		str = str.replaceAll("'$", "");
		while (m.find()) {
			String s = m.group();
			str = str.replaceAll("\\{msg_" + s + "}", getString(s, player));
		}
		if (HeadsPlus.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			str = PlaceholderAPI.setPlaceholders(player, str);
		}
		str = ChatColor.translateAlternateColorCodes('&', str);
		return str;
    }

    public void setPlayerLocale(Player player) {
	    String locale = getLocale(player);
	    String first = locale.split("_")[0].toLowerCase();
	    if (locales.containsKey(first)) {
	        players.put(player, locales.get(first));
        }

    }

    private static String getLocale(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return String.valueOf(entityPlayer.getClass().getField("locale").get(entityPlayer));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
        	HeadsPlus.getInstance().getServer().getLogger().info("Whoops, have an error to report...");
			try {
				new DebugFileCreator().createReport(e, "Setting Smart Locale (Retreiving)");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
        }
        return "en_us";
    }
}
