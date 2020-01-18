package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class AbstractListList extends AbstractListCommand {

    public abstract String getExtendedType();

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        if (args.length > 1) {
            if (args[1].matches("^[0-9]+$")) {
                return "";
            } else {
                return hpc.getString("commands.errors.invalid-input-int", sender);
            }
        } else {
            return "";
        }
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            List<String> wl = getList();
            int page;
            if (args.length == 1) {
                page = 1;
            } else {
                page = Integer.parseInt(args[1]);
            }
            if (wl.isEmpty()) {
                sender.sendMessage(hpc.getString("commands." + getFullName() + "." + "empty-" + getListType(), sender));
                return true;
            }
            sender.sendMessage(HeadsPlusConfigTextMenu.BlacklistTranslator.translate(sender, getExtendedType(), getType(), wl, page));


        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (" + getClass().getAnnotation(CommandInfo.class).commandname() + ")", true, sender);
        }
        return true;
    }
}
