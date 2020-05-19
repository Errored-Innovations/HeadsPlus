package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.util.CachedValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListAdd extends AbstractListCommand {

    public AbstractListAdd(HeadsPlus hp) {
        super(hp);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            if (args.length > 1) {
                if (CachedValues.PLAYER_NAME.matcher(args[1]).matches()) {
                    String aHead = args[1].toLowerCase();
                    if (getList().contains(aHead)) {
                        sender.sendMessage(hpc.getString("commands." + getFullName() + "." + getType() + "-a-add", sender));
                    } else {
                        getList().add(aHead);
                        config.getConfig().set(getPath(), getList());
                        config.save();
                        sender.sendMessage(hpc.getString("commands." + getFullName() + "." + getType() + "-added-" + getListType(), sender).replaceAll("\\{name}", args[1]).replaceAll("\\{player}", args[1]));
                    }
                } else {
                    sender.sendMessage(hpc.getString("commands.head.alpha-names", sender));
                }
            } else {
                hpc.sendMessage("commands.errors.invalid-args", sender);
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (" + getClass().getAnnotation(CommandInfo.class).commandname() + ")", true, sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(), results);
        }
        return results;
    }
}
