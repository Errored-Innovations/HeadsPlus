package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListDelete;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;

import java.util.List;

@CommandInfo(
        commandname = "whitelistwdel",
        permission = "headsplus.maincommand.whitelistw.delete",
        subcommand = "Whitelistwdel",
        maincommand = true,
        usage = "/hp whitelistwdel <World Name>"
)
public class WhitelistwDelete extends AbstractListDelete {

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistwDelete();
    }

    @Override
    public List<String> getList() {
        return config.getWorldWhitelist().list;
    }

    @Override
    public String getPath() {
        return "whitelist.world.list";
    }

    @Override
    public String getListType() {
        return "wl";
    }

    @Override
    public String getType() {
        return "world";
    }
}
