package io.github.thatsmusic99.headsplus.commands.maincommand.lists.blacklist;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.lists.AbstractListAdd;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        commandname = "blacklistadd",
        permission = "headsplus.maincommand.blacklist.add",
        subcommand = "Blacklistadd",
        maincommand = true,
        usage = "/hp blacklistadd <Username>"
)
public class BlacklistAdd extends AbstractListAdd {

    // S
    @Override
    public String getCmdDescription(CommandSender cs) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.blacklistadd", cs);
    }

    @Override
    public List<String> getList() {
        return config.getHeadsBlacklist().list;
    }

    @Override
    public String getPath() {
        return "blacklist.default.list";
    }

    @Override
    public String getListType() {
        return "bl";
    }

    @Override
    public String getType() {
        return "head";
    }

    @Override
    public String getFullName() {
        return "blacklist";
    }
}
