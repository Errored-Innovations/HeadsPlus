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
import java.util.List;

@CommandInfo(
		commandname = "blacklistwdel",
		permission = "headsplus.maincommand.blacklistw.delete",
		subcommand = "Blacklistwdel",
		maincommand = true,
		usage = "/hp blacklistwdel <World Name>"
)
public class BlacklistwDelete implements IHeadsPlusCommand {

	// A
	private final HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
	private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descBlacklistwDelete();
	}

	@Override
	public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
		HashMap<Boolean, String> h = new HashMap<>();
		if (args.length > 1) {
			if (args[1].matches("^[A-Za-z0-9_]+$")) {
				h.put(true, "");
			} else {
				h.put(false, hpc.getString("alpha-names"));
			}
		} else {
			h.put(false, hpc.getString("invalid-args"));
		}

		return h;
	}

	@Override
	public boolean fire(String[] args, CommandSender sender) {
		if (args.length > 1) {
			if (args[1].matches("^[A-Za-z0-9_]+$")) {
                try {
                    List<String> blacklist = config.getBlacklist().list;
                    String rHead = args[1].toLowerCase();
                    if (blacklist.contains(rHead)) {
                        blacklist.remove(rHead);
                        config.getConfig().set("blacklist.world.list", blacklist);
                        config.save();
                        sender.sendMessage(hpc.getString("world-removed-bl").replaceAll("\\{name}", args[1]));
                    } else {
                        sender.sendMessage(hpc.getString("world-a-removed-bl"));

                    }
                } catch (Exception e) {
                	new DebugPrint(e, "Subcommand (blacklistwdel)", true, sender);
                }
			} else {
				sender.sendMessage(hpc.getString("alpha-names"));
			}
		} else {
            sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + getClass().getAnnotation(CommandInfo.class).usage());
        }
		return true;
	}
}
