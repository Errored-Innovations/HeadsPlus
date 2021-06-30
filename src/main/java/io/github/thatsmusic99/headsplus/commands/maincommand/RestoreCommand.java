package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.customheads.ConfigCustomHeads;
import io.github.thatsmusic99.headsplus.config.defaults.HeadsXEnums;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "restore",
        permission = "headsplus.maincommand.restore",
        subcommand = "restore",
        maincommand = true,
        usage = "/hp restore <Head Name>"
)
public class RestoreCommand implements IHeadsPlusCommand {
    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlusMessagesManager.get().getString("descriptions.hp.restore", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        if (args.length > 1) {
            try {
                HeadsXEnums headToBeAdded = HeadsXEnums.valueOf(args[1].toUpperCase());
                ConfigCustomHeads.get().set("heads." + headToBeAdded.name, null);
                ConfigCustomHeads.get().addHead(headToBeAdded.texture,
                        true,
                        headToBeAdded.displayName,
                        headToBeAdded.section,
                        "default",
                        true);
                HeadsPlusMessagesManager.get().sendMessage("commands.restore.restored-head", sender, "{head}", args[1]);
                return true;
            } catch (IllegalArgumentException ex) {
                HeadsPlusMessagesManager.get().sendMessage("commands.restore.invalid-head", sender, "{head}", args[1]);
                return true;
            }
        } else {
            HeadsPlusMessagesManager.get().sendMessage("commands.errors.invalid-args", sender);
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            List<String> headNames = new ArrayList<>();
            for (HeadsXEnums head : HeadsXEnums.values()) {
                headNames.add(head.name().toLowerCase());
            }
            StringUtil.copyPartialMatches(args[1], headNames, results);
        }
        return results;
    }
}
