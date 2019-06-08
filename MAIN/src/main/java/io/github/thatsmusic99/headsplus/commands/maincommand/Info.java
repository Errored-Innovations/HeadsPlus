package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

@CommandInfo(
		commandname = "info",
		permission = "headsplus.maincommand.info",
		subcommand = "Info",
		maincommand = true,
		usage = "/hp info"
)
public class Info implements IHeadsPlusCommand {

	// D
	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descInfo();
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
			sender.sendMessage(HeadsPlusConfigTextMenu.InfoTranslator.translate());
        } catch (Exception e) {
		    new DebugPrint(e, "Subcommand (info)", true, sender);
        }
		return true;
	}
}
