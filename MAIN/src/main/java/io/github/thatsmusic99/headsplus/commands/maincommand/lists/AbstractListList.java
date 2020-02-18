package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListList extends AbstractListCommand {

    public abstract String getExtendedType();

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        try {
            List<String> wl = getList();
            int page;
            if (args.length > 1) {
                if (args[1].matches("^[0-9]+$")) {
                    page = Integer.parseInt(args[1]);
                } else {
                    sender.sendMessage(hpc.getString("commands.errors.invalid-input-int", sender));
                    return false;
                }
            } else {
                page = 1;
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
