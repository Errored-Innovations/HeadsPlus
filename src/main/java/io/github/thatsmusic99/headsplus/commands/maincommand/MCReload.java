package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
                        cs.reloadC();
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}


