package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigTextMenus;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "profile",
        permission = "headsplus.maincommand.profile",
        maincommand = true,
        usage = "/hp profile [Player]",
        descriptionPath = "descriptions.hp.profile"
)
public class ProfileCommand implements IHeadsPlusCommand {

    @Override
    public boolean onCommand(CommandSender cs, @NotNull Command command, @NotNull String label, String[] args) {
        String name = cs.getName();
        if (args.length != 1) {
            name = args[1];
        }

        HPUtils.getOfflinePlayer(name).thenAccept(player -> {
            try {
                if (cs instanceof Player) {
                    if (cs.getName().equalsIgnoreCase(player.getName())) {
                        ConfigTextMenus.ProfileTranslator.translate(player, cs).thenAccept(cs::sendMessage);
                    } else {
                        if (cs.hasPermission("headsplus.maincommand.profile.others")) {
                            ConfigTextMenus.ProfileTranslator.translate(player, cs).thenAccept(cs::sendMessage);
                        } else {
                            MessagesManager.get().sendMessage("commands.errors.no-perm", cs);
                        }
                    }
                } else {
                    if (cs.getName().equalsIgnoreCase(player.getName())) {
                        // Not a player
                        cs.sendMessage(MessagesManager.get().getString("commands.profile.cant-view-data"));
                    } else {
                        ConfigTextMenus.ProfileTranslator.translate(player, cs).thenAccept(cs::sendMessage);
                    }
                }
            } catch (Exception e) {
                DebugPrint.createReport(e, "Subcommand (profile)", true, cs);
            }
        });
        return true;
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(sender), results);
        }
        return results;
    }
}
