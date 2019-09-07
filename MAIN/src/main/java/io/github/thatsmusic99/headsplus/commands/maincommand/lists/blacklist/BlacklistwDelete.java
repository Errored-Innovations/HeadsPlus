package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListDelete;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;

import java.util.List;

@CommandInfo(
		commandname = "blacklistwdel",
		permission = "headsplus.maincommand.blacklistw.delete",
		subcommand = "Blacklistwdel",
		maincommand = true,
		usage = "/hp blacklistwdel <World Name>"
)
public class BlacklistwDelete extends AbstractListDelete {

	// A

	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descBlacklistwDelete();
	}

	@Override
	public List<String> getList() {
		return config.getWorldBlacklist().list;
	}

	@Override
	public String getPath() {
		return "blacklist.world.list";
	}

	@Override
	public String getListType() {
		return "bl";
	}

	@Override
	public String getType() {
		return "world";
	}
}
