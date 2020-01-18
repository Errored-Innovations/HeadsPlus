package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public abstract class AbstractListDelete extends AbstractListCommand {

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        if (args.length > 1) {
            if (args[1].matches("^[A-Za-z0-9_]+$")) {
                return "";
            } else {
                return hpc.getString("commands.head.alpha-names", sender);
            }
        } else {
            return hpc.getString("commands.errors.invalid-args", sender);
        }
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            String rHead = args[1].toLowerCase();
            if (getList().contains(rHead)) {
                getList().remove(rHead);
                config.getConfig().set(getPath(), getList());
                config.save();
                sender.sendMessage(hpc.getString("commands." + getFullName() + "." + getType() + "-removed-" + getListType(), sender).replaceAll("\\{player}", args[1]).replaceAll("\\{name}", args[1]));
            } else {
                sender.sendMessage(hpc.getString("commands." + getFullName() + "." + getType() + "-a-removed-" + getListType(), sender));
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (" + getClass().getAnnotation(CommandInfo.class).commandname() + ")", true, sender);
        }
        return true;
    }
}
