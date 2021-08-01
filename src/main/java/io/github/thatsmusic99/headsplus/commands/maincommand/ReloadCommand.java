package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.FeatureConfig;
import io.github.thatsmusic99.headsplus.config.HPConfig;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.managers.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "reload",
        permission = "headsplus.maincommand.reload",
        maincommand = true,
        usage = "/hp reload",
        descriptionPath = "descriptions.hp.reload"
)
public class ReloadCommand implements IHeadsPlusCommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        HeadsPlusMessagesManager.get().sendMessage("commands.reload.reloading-message", sender);
        new BukkitRunnable() {
            @Override
            public void run() {
                HeadManager.get().reset();
                MaskManager.get().reset();
                SellableHeadsManager.get().reset();
                for (HPConfig cs : HeadsPlus.get().getConfigs()) {
                    try {
                        if (cs instanceof FeatureConfig) {
                            FeatureConfig featureConfig = (FeatureConfig) cs;
                            if (!featureConfig.shouldLoad()) continue;
                            if (!featureConfig.isLoaded()) {
                                featureConfig.load();
                                continue;
                            }
                        }
                        cs.reload();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                HPPlayer.players.clear();
                EntityDataManager.init();
                CraftingManager.get().reload();
                ChallengeManager.get().reload();
                HeadsPlus.get().restartMessagesManager();
                HeadsPlus.get().initiateEvents();
                HeadsPlusMessagesManager.get().sendMessage("commands.reload.reload-message", sender);
            }
        }.runTaskLaterAsynchronously(HeadsPlus.get(), 2);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean shouldEnable() {
        return true;
    }
}


