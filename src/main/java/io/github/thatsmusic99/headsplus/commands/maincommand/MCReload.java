package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.configurationmaster.CMFile;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
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
public class MCReload implements IHeadsPlusCommand {

    @Override
    public String getCmdDescription(CommandSender sender) {
        return HeadsPlusMessagesManager.get().getString("descriptions.hp.reload");
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        HeadsPlusMessagesManager.get().sendMessage("commands.reload.reloading-message", sender);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (CMFile cs : HeadsPlus.get().getConfigs()) {
                    cs.reload();
                }
                HPPlayer.players.clear();
                EntityDataManager.init();
                HeadsPlus.get().restartMessagesManager();
                HeadsPlusMessagesManager.get().sendMessage("commands.reload.reload-message", sender);
            }
        }.runTaskLaterAsynchronously(HeadsPlus.get(), 2);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}


