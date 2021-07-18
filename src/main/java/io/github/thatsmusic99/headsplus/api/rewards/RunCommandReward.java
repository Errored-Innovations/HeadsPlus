package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class RunCommandReward extends Reward {

    private List<String> commands;

    public RunCommandReward(int xp, List<String> commands) {
        super(xp);
        this.commands = commands;
    }

    public static RunCommandReward fromConfigSection(String id, ConfigSection section) {
        return null;
    }

    @Override
    public void rewardPlayer(Player player) {
        super.rewardPlayer(player);
        for (String command : commands) {
            if (command.startsWith("player>")) {
                Bukkit.dispatchCommand(player, command.substring(8).replaceAll("\\{player}", player.getName()));
                return;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("\\{player}", player.getName()));
        }
    }

    @Override
    public String getDefaultRewardString(Player player) {
        return null;
    }
}
