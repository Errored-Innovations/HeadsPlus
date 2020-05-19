package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListDelete;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
		commandname = "blacklistwdel",
		permission = "headsplus.maincommand.blacklistw.delete",
		subcommand = "Blacklistwdel",
		maincommand = true,
		usage = "/hp blacklistwdel <World Name>"
)
public class BlacklistwDelete extends AbstractListDelete {
	public BlacklistwDelete(HeadsPlus hp) {
		super(hp);
	}

	@Override
	public String getCmdDescription(CommandSender sender) {
		return hpc.getString("descriptions.hp.blacklistwdel", sender);
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

	@Override
	public String getFullName() {
		return "blacklist";
	}

}
