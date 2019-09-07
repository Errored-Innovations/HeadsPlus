package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListDelete;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;

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
	public String getCmdDescription() {
		return LocaleManager.getLocale().descBlacklistDelete();
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
}
