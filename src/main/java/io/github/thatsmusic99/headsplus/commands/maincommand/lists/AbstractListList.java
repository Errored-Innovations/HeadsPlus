package io.github.thatsmusic99.headsplus.commands.maincommand.lists;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListList extends AbstractListCommand {

    public AbstractListList(HeadsPlus hp) {
        super(hp);
    }

    public abstract String getExtendedType();

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        List<String> wl = getList();
        int page;
        if (args.length > 1) {
            page = HPUtils.isInt(args[1]);
        } else {
            page = 1;
        }
        if (wl.isEmpty()) {
            hpc.sendMessage("commands." + getFullName() + "." + "empty-" + getListType(), sender);
            return true;
        }
        sender.sendMessage(HeadsPlusConfigTextMenu.BlacklistTranslator.translate(sender, getExtendedType(), getType(), wl, page));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
