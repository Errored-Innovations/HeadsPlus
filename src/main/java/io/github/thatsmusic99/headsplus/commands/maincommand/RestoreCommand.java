package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigHeads;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
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
        maincommand = true,
        usage = "/hp restore <Head Name>",
        descriptionPath = "descriptions.hp.restore"
)
public class RestoreCommand implements IHeadsPlusCommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String[] args) {
        if (args.length > 1) {
            try {
                HeadsXEnums headToBeAdded = HeadsXEnums.valueOf(args[1].toUpperCase());
                ConfigHeads.get().set("heads." + headToBeAdded.name, null);
                ConfigHeads.get().set("heads." + headToBeAdded.name + ".display-name", headToBeAdded.displayName);
                ConfigHeads.get().set("heads." + headToBeAdded.name + ".texture", headToBeAdded.texture);
                MessagesManager.get().sendMessage("commands.restore.restored-head", sender, "{head}", args[1]);
                return true;
            } catch (IllegalArgumentException ex) {
                MessagesManager.get().sendMessage("commands.restore.invalid-head", sender, "{head}", args[1]);
                return true;
            }
        } else {
            MessagesManager.get().sendMessage("commands.errors.invalid-args", sender);
            return false;
        }
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
                                      @NotNull String[] args) {
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
