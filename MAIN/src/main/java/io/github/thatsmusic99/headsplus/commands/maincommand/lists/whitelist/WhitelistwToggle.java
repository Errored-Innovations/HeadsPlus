package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListToggle;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;

import java.util.List;

@CommandInfo(
        commandname = "whitelistw",
        permission = "headsplus.maincommand.whitelistw.toggle",
        subcommand = "Whitelistwl",
        maincommand = true,
        usage = "/hp whitelistwl [On|Off]"
)
public class WhitelistwToggle extends AbstractListToggle {

    private final HeadsPlusMainConfig config = HeadsPlus.getInstance().getConfiguration();
    private final HeadsPlusMessagesConfig hpc = HeadsPlus.getInstance().getMessagesConfig();

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistwToggle();
    }

    @Override
    public HeadsPlusMainConfig.SelectorList getSelList() {
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
}
