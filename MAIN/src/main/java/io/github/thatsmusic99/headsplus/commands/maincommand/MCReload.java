package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

@CommandInfo(
        commandname = "reload",
        permission = "headsplus.maincommand.reload",
        subcommand = "Reload",
        maincommand = true,
        usage = "/hp reload"
)
public class MCReload implements IHeadsPlusCommand{

    // O

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.reload");
    }

    @Override
    public String isCorrectUsage(String[] args, CommandSender sender) {
        return "";
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        HeadsPlusMessagesManager m = HeadsPlus.getInstance().getMessagesConfig();
        String reloadM = m.getString("commands.reload.reload-message");
        String reloadingM = m.getString("commands.reload.reloading-message");
        sender.sendMessage(reloadingM);
        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (ConfigSettings cs : HeadsPlus.getInstance().getConfigs()) {
                        cs.reloadC(false);
                    }
                    HPPlayer.players.clear();
                    HeadsPlus.getInstance().reloadDE();
                    HeadsPlus.getInstance().restartMessagesManager();
                    sender.sendMessage(reloadM);
                }
            }.runTaskLaterAsynchronously(HeadsPlus.getInstance(), 2);
        } catch (Exception e) {
            DebugPrint.createReport(e, "Subcommand (reload)", true, sender);
        }
        return true;
    }
}


