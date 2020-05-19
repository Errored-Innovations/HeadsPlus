package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListAdd;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        commandname = "whitelistadd",
        permission = "headsplus.maincommand.whitelist.add",
        subcommand = "Whitelistadd",
        maincommand = true,
        usage = "/hp whitelistadd <Username>"
)
public class WhitelistAdd extends AbstractListAdd {

    public WhitelistAdd(HeadsPlus hp) {
        super(hp);
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.whitelistadd", sender);
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
