package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListDelete extends AbstractListCommand {

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            if (args.length > 1) {
                if (args[1].matches("^[A-Za-z0-9_]+$")) {
                    String rHead = args[1].toLowerCase();
                    if (getList().contains(rHead)) {
                        getList().remove(rHead);
                        config.getConfig().set(getPath(), getList());
                        config.save();
                        sender.sendMessage(hpc.getString("commands." + getFullName() + "." + getType() + "-removed-" + getListType(), sender).replaceAll("\\{player}", args[1]).replaceAll("\\{name}", args[1]));
                    } else {
                        sender.sendMessage(hpc.getString("commands." + getFullName() + "." + getType() + "-a-removed-" + getListType(), sender));
                    }
                    return true;
                } else {
                    sender.sendMessage(hpc.getString("commands.head.alpha-names", sender));
                }
            } else {
                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (" + getClass().getAnnotation(CommandInfo.class).commandname() + ")", true, sender);
        }
        return false;
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
