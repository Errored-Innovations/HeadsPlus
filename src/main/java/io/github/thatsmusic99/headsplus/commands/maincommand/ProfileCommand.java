package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigTextMenus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
        subcommand = "Profile",
        maincommand = true,
        usage = "/hp profile [Player]"
)
public class ProfileCommand implements IHeadsPlusCommand {

    private String prof(OfflinePlayer p, CommandSender sender) throws SQLException {
        try {
            HPPlayer pl = HPPlayer.getHPPlayer(p);
            return ConfigTextMenus.ProfileTranslator.translate(pl, sender);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return HeadsPlusMessagesManager.get().getString("commands.errors.no-data", sender);
        }
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlusMessagesManager.get().getString("descriptions.hp.profile", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender cs) {
        try {
            HeadsPlus hp = HeadsPlus.get();
            OfflinePlayer p;
            if (args.length == 1) {
                // TODO: better on a separate thread
                p = Bukkit.getOfflinePlayer(cs.getName());
            } else {
                p = Bukkit.getOfflinePlayer(args[1]);
            }
            if (cs instanceof Player) {
                if (cs.getName().equalsIgnoreCase(p.getName())) {
                    cs.sendMessage(prof(p, cs));
                } else {
                    if (cs.hasPermission("headsplus.maincommand.profile.others")) {
                        cs.sendMessage(prof(p, cs));
                    } else {
                        HeadsPlusMessagesManager.get().sendMessage("commands.errors.no-perm", cs);
                    }
                }
            } else {
                if (cs.getName().equalsIgnoreCase(p.getName())) {
                    // Not a player
                    HeadsPlusMessagesManager.get().sendMessage("commands.profile.cant-view-data", cs);
                } else {
                    cs.sendMessage(prof(p, cs));
                }
            }
        } catch (SQLException e) {
            DebugPrint.createReport(e, "Subcommand (profile)", true, cs);
        }


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
