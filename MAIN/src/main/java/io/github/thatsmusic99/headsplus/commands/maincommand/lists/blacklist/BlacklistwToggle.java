package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListToggle;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;

import java.util.List;

@CommandInfo(
        commandname = "blacklistw",
        permission = "headsplus.maincommand.blacklistw.toggle",
        subcommand = "Blacklistw",
        maincommand = true,
        usage = "/hp blacklistw [On|Off]"
)
public class BlacklistwToggle extends AbstractListToggle {

    // A

	@Override
	public String getCmdDescription() {
		return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.blacklistw");
	}

    @Override
    public HeadsPlusMainConfig.SelectorList getSelList() {
        return config.getWorldBlacklist();
    }

    @Override
    public List<String> getList() {
        return getSelList().list;
    }

    @Override
    public String getPath() {
        return "blacklist.world.enabled";
    }

    @Override
    public String getListType() {
        return "blw";
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
