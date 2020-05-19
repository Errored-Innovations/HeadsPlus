package io.github.thatsmusic99.headsplus.commands.maincommand.lists.whitelist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListAdd;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        commandname = "whitelistwadd",
        permission = "headsplus.maincommand.whitelistw.add",
        subcommand = "Whitelistwadd",
        maincommand = true,
        usage = "/hp whitelistwadd <World Name>"
)
public class WhitelistwAdd extends AbstractListAdd {

    public WhitelistwAdd(HeadsPlus hp) {
        super(hp);
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.hp.whitelistwadd", sender);
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

    @Override
    public String getFullName() {
        return "whitelist";
    }

}

