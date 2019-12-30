package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListToggle;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;

import java.util.List;

@CommandInfo(
		commandname = "blacklist",
		permission = "headsplus.maincommand.blacklist.toggle",
		subcommand = "Blacklist",
		maincommand = true,
		usage = "/hp blacklistadd [On|Off]"
)
public class BlacklistToggle extends AbstractListToggle {

	// F

	@Override
	public String getCmdDescription() {
		return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.blacklist");
	}


	@Override
	public HeadsPlusMainConfig.SelectorList getSelList() {
		return config.getHeadsBlacklist();
	}

	@Override
	public List<String> getList() {
		return getSelList().list;
	}

	@Override
	public String getPath() {
		return "blacklist.default.enabled";
	}

	@Override
	public String getListType() {
		return "bl";
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public String getFullName() {
		return "blacklist";
	}
}
