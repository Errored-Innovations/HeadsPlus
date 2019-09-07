package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public abstract class AbstractListAdd extends AbstractListCommand {

    @Override
    public HashMap<Boolean, String> isCorrectUsage(String[] args, CommandSender sender) {
        HashMap<Boolean, String> h = new HashMap<>();
        if (args.length > 1) {
            if (args[1].matches("^[A-Za-z0-9_]+$")) {
                h.put(true, "");
            } else {
                h.put(false, hpc.getString("alpha-names"));
            }
        } else {
            h.put(false, hpc.getString("invalid-args"));
        }

        return h;
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            String aHead = args[1].toLowerCase();
            if (getList().contains(aHead)) {
                sender.sendMessage(hpc.getString(getType() + "-a-add"));
            } else {
                getList().add(aHead);
                config.getConfig().set(getPath(), getList());
                config.save();
                sender.sendMessage(hpc.getString(getType() + "-added-" + getListType()).replaceAll("\\{name}", args[1]).replaceAll("\\{player}", args[1]));
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (" + getClass().getAnnotation(CommandInfo.class).commandname() + ")", true, sender);
        }
        return true;
    }
}
