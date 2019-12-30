package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListList;

import java.util.List;

@CommandInfo(
        commandname = "whitelistwl",
        permission = "headsplus.maincommand.whitelistw.list",
        subcommand = "Whitelistwl",
        maincommand = true,
        usage = "/hp whitelistwl [Page No.]"
)
public class WhitelistwList extends AbstractListList {

    @Override
    public String getCmdDescription() {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.whitelistwl");
    }


    @Override
    public String getExtendedType() {
        return "whitelist";
    }

    @Override
    public List<String> getList() {
        return config.getWorldWhitelist().list;
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
        return "head";
    }

    @Override
    public String getFullName() {
        return "whitelist";
    }

}
