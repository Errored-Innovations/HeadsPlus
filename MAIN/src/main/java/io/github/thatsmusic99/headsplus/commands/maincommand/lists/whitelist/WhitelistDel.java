package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListDelete;

import java.util.List;

@CommandInfo(
        commandname = "whitelistdel",
        permission = "headsplus.maincommand.whitelist.delete",
        subcommand = "Whitelistdel",
        maincommand = true,
        usage = "/hp whitelistdel <Username>"
)
public class WhitelistDel extends AbstractListDelete {

    @Override
    public String getCmdDescription() {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.whitelistdel");
    }

    @Override
    public List<String> getList() {
        return config.getHeadsWhitelist().list;
    }

    @Override
    public String getPath() {
        return "whitelist.default.list";
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
