package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListList;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;

import java.util.List;

@CommandInfo(
        commandname = "blacklistl",
        permission = "headsplus.maincommand.blacklist.list",
        subcommand = "Blacklistl",
        maincommand = true,
        usage = "/hp blacklistl [Page No.]"
)
public class BlacklistList extends AbstractListList {

    // L

	@Override
	public String getCmdDescription() {
		return LocaleManager.getLocale().descBlacklistList();
	}

    @Override
    public String getExtendedType() {
        return "blacklist";
    }

    @Override
    public List<String> getList() {
        return config.getHeadsBlacklist().list;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getListType() {
        return "bl";
    }

    @Override
    public String getType() {
        return "default";
    }
}
