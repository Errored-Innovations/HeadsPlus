package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class HeadsPlusMessagesManager {

    private static YamlConfiguration config;

	public HeadsPlusMessagesManager() {
	    HeadsPlus hp = HeadsPlus.getInstance();
	    HeadsPlusMainConfig mainConfig = hp.getConfiguration();
	    String locale = mainConfig.getConfig().getString("locale");
	    try {
	        config = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, locale + ".yml"));
        } catch (Exception e) {
	        hp.getLogger().info("Failed to load the locale settings! This is caused by an invalid name provided. Setting locale to en_us...");
            config = YamlConfiguration.loadConfiguration(new File(hp.getDataFolder() + File.separator + "locale" + File.separator, "en_us.yml"));
        }
    }
	public String getString(String path) {
	    String str = config.getString(path);
	    if (str == null) return "";
	    str = str.replaceAll("\\{header}", config.getString("prefix"));
	    str = str.replaceAll("''", "'");
	    str = str.replaceAll("^'", "");
	    str = str.replaceAll("'$", "");
	    str = ChatColor.translateAlternateColorCodes('&', str);
        return str;
    }

}
