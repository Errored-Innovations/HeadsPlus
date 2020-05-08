package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
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

    // F
    private String prof(OfflinePlayer p, CommandSender sender) throws SQLException {
        try {
            HPPlayer pl = HPPlayer.getHPPlayer(p);
            return HeadsPlusConfigTextMenu.ProfileTranslator.translate(pl, sender);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return HeadsPlus.getInstance().getMessagesConfig().getString("commands.errors.no-data", sender);
        }
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.profile", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender cs) {
        try {
            HeadsPlus hp = HeadsPlus.getInstance();
            OfflinePlayer p;
            NMSManager nms = hp.getNMS();
            if (args.length == 1) {
                p = nms.getOfflinePlayer(cs.getName());
            } else {
                p = nms.getOfflinePlayer(args[1]);
            }
            if (cs instanceof Player) {
                if (cs.getName().equalsIgnoreCase(p.getName())) {
                    cs.sendMessage(prof(p, cs));
                } else {
                    if (cs.hasPermission("headsplus.maincommand.profile.others")) {
                        cs.sendMessage(prof(p, cs));
                    } else {
                        cs.sendMessage(hp.getMessagesConfig().getString("commands.errors.no-perm"));
                    }
                }
            } else {
                if (cs.getName().equalsIgnoreCase(p.getName())) {
                    cs.sendMessage(hp.getMessagesConfig().getString("commands.profile.cant-view-data"));
                } else {
                    cs.sendMessage(prof(p, cs));
                }
            }
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (profile)", true, cs);
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(), results);
        }
        return results;
    }
}
