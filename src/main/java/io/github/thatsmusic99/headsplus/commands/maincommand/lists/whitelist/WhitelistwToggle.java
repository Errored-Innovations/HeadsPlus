package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListToggle;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        commandname = "whitelistw",
        permission = "headsplus.maincommand.whitelistw.toggle",
        subcommand = "Whitelistwl",
        maincommand = true,
        usage = "/hp whitelistwl [On|Off]"
)
public class WhitelistwToggle extends AbstractListToggle {

    private final MainConfig config = HeadsPlus.get().getConfiguration();
    private final HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();

    public WhitelistwToggle(HeadsPlus hp) {
        super(hp);
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.whitelistw", sender);
    }

    @Override
    public MainConfig.SelectorList getSelList() {
        return config.getWorldWhitelist();
    }

    @Override
    public List<String> getList() {
        return getSelList().list;
    }

    @Override
    public String getPath() {
        return "whitelist.world.enabled";
    }

    @Override
    public String getListType() {
        return "wlw";
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getFullName() {
        return "whitelist";
    }
}
