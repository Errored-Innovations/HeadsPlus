package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

@CommandInfo(
		commandname = "reload",
		permission = "headsplus.maincommand.reload",
		subcommand = "Reload",
		maincommand = true,
		usage = "/hp reload"
)
public class MCReload implements IHeadsPlusCommand{

	// O

	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descMCReload();
	}

	@Override
	public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
		HashMap<Boolean, String> h = new HashMap<>();
		h.put(true, "");
		return h;
	}

	@Override
	public boolean fire(String[] args, CommandSender sender) {
		HeadsPlusMessagesConfig m = HeadsPlus.getInstance().getMessagesConfig();
		String reloadM = m.getString("reload-message");
		String reloadingM = m.getString("reloading-message");
		sender.sendMessage(reloadingM);
		try {
			new BukkitRunnable() {
                @Override
                public void run() {
                    for (ConfigSettings cs : HeadsPlus.getInstance().getConfigs()) {
                        cs.reloadC(false);
                    }
                    HPPlayer.players.clear();
                    HeadsPlus.getInstance().reloadDE();
                    sender.sendMessage(reloadM);
                }
            }.runTaskLaterAsynchronously(HeadsPlus.getInstance(), 2);
		} catch (Exception e) {
		    new DebugPrint(e, "Subcommand (reload)", true, sender);
		}
		return true;
	}
}


