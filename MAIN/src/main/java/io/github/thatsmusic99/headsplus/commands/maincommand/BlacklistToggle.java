package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

@CommandInfo(
		commandname = "blacklist",
		permission = "headsplus.maincommand.blacklist.toggle",
		subcommand = "Blacklist",
		maincommand = true,
		usage = "/hp blacklistadd [On|Off]"
)
public class BlacklistToggle implements IHeadsPlusCommand {

	// F
	private final HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
	private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descBlacklistToggle();
	}

	@Override
	public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
		HashMap<Boolean, String> h = new HashMap<>();
		h.put(true, "");
		return h;
	}

	@Override
	public boolean fire(String[] args, CommandSender sender) {
		try {
			if (args.length == 1) {
                config.getConfig().set("blacklist.default.enabled", config.getHeadsBlacklist().enabled = !config.getHeadsBlacklist().enabled);
				config.save();
				sender.sendMessage(config.getHeadsBlacklist().enabled ? "bl-on" : "bl-off");
			} else {
				if (args[1].equalsIgnoreCase("on")) {
					if (!config.getHeadsBlacklist().enabled) {
                        config.getConfig().set("blacklist.default.enabled", config.getHeadsBlacklist().enabled = true);
						config.save();
						sender.sendMessage(hpc.getString("bl-on"));
					} else {
						sender.sendMessage(hpc.getString("bl-a-on"));
					}

				} else if (args[1].equalsIgnoreCase("off")) {
					if (config.getHeadsBlacklist().enabled) {
                        config.getConfig().set("blacklist.default.enabled", config.getHeadsBlacklist().enabled = false);
						config.save();
						sender.sendMessage(hpc.getString("bl-off"));
					} else {
						sender.sendMessage(hpc.getString("bl-a-off"));
					}
				} else {
					sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + getClass().getAnnotation(CommandInfo.class).usage());
				}
			}
		} catch (Exception e) {
            new DebugPrint(e, "Subcommand (blacklist)", true, sender);
		}
        return true;
	}
}
