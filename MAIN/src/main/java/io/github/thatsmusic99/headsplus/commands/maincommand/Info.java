package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
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
		return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.info");
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
		    DebugPrint.createReport(e, "Subcommand (info)", true, sender);
        }
		return true;
	}
}
