package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RunCommandReward extends Reward<List<String>> {

    public RunCommandReward(long xp, List<String> commands) {
        super(commands, xp);
    }

    public static RunCommandReward fromConfigSection(String id, ConfigSection section) {
        if (!section.contains("base-value") && !section.contains("reward-value"))
            throw new IllegalStateException("Reward type RUN_COMMAND for reward " + id + " must have a base-value " +
                    "option!");
        return new RunCommandReward(section.getLong("base-xp", 0),
                section.getList("base-value", section.getList("reward-value")));
    }

    @Override
    public void rewardPlayer(Challenge challenge, @NotNull Player player) {
        super.rewardPlayer(challenge, player);
        for (String command : this.reward) {
            if (command.startsWith("player>")) {
                Bukkit.dispatchCommand(player, command.substring(8).replaceAll("\\{player}", player.getName()));
                return;
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("\\{player}", player.getName()));
        }
    }

    @Override
    public String getDefaultRewardString(Player player, int difficulty) {
        return null;
    }
}
