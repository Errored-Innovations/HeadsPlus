package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import org.bukkit.command.CommandSender;

public abstract class AbstractListAdd extends AbstractListCommand {

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
            String aHead = args[1].toLowerCase();
            if (getList().contains(aHead)) {
                sender.sendMessage("commands." + getFullName() + "." + hpc.getString(getType() + "-a-add", sender));
            } else {
                getList().add(aHead);
                config.getConfig().set(getPath(), getList());
                config.save();
                sender.sendMessage("commands." + getFullName() + "." + hpc.getString(getType() + "-added-" + getListType(), sender).replaceAll("\\{name}", args[1]).replaceAll("\\{player}", args[1]));
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (" + getClass().getAnnotation(CommandInfo.class).commandname() + ")", true, sender);
        }
        return true;
    }
}
