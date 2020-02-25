package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListDelete;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
		commandname = "blacklistdel",
		permission = "headsplus.maincommand.blacklist.delete",
		subcommand = "Blacklistdel",
		maincommand = true,
		usage = "/hp blacklistdel <Username>"
)
public class BlacklistDelete extends AbstractListDelete {

	// E

	@Override
	public String getCmdDescription(CommandSender cs) {
		return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.blacklistdel", cs);
	}

	@Override
	public List<String> getList() {
		return config.getHeadsBlacklist().list;
	}

	@Override
	public String getPath() {
		return "blacklist.default.list";
	}

	@Override
	public String getListType() {
		return "bl";
	}

	@Override
	public String getType() {
		return "head";
	}

	@Override
	public String getFullName() {
		return "blacklist";
	}
}
