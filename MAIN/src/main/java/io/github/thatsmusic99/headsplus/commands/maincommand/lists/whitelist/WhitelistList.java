package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListList;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;

import java.util.List;

@CommandInfo(
        commandname = "whitelistl",
        permission = "headsplus.maincommand.whitelist.list",
        subcommand = "Whitelistl",
        maincommand = true,
        usage = "/hp whitelistl [Page No.]"
)
public class WhitelistList extends AbstractListList {

    @Override
    public String getCmdDescription() {
        return LocaleManager.getLocale().descWhitelistList();
    }

    @Override
    public String getExtendedType() {
        return "whitelist";
    }

    @Override
    public List<String> getList() {
        return config.getHeadsWhitelist().list;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getListType() {
        return "wl";
    }

    @Override
    public String getType() {
        return "default";
    }
}
