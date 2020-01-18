package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListList;
import org.bukkit.command.CommandSender;

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
	public String getCmdDescription(CommandSender cs) {
		return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.blacklistl", cs);
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

    @Override
    public String getFullName() {
        return "blacklist";
    }
}
