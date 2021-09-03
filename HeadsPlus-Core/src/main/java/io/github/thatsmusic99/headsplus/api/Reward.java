package io.github.thatsmusic99.headsplus.api;

import headsplus.api.rewards.*;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.rewards.*;
import org.bukkit.entity.Player;

public abstract class Reward {

    private final long xp;
    private String message;
    private boolean useMultiplier;

    public Reward(long xp) {
        this.xp = xp;
    }

    public static Reward fromConfigSection(String id, ConfigSection section) {
        String type = section.getString("type");
        if (type == null) type = section.getString("reward-type");
        if (type == null)
            throw new IllegalStateException("There is no reward type for " + id + "!");

        Reward reward;
        switch (type.toLowerCase()) {
            case "eco":
                reward = EconomyReward.fromConfigSection(id, section);
                break;
            case "give_item":
                reward = ItemReward.fromConfigSection(id, section);
                break;
            case "add_group":
                reward = AddGroupReward.fromConfigSection(id, section);
                break;
            case "remove_group":
                reward = RemoveGroupReward.fromConfigSection(id, section);
                break;
            case "run_command":
                reward = RunCommandReward.fromConfigSection(id, section);
                break;
            default:
                throw new IllegalStateException("No such reward type " + type + " for " + id + "!");
        }

        String customMessage = section.getString("reward-string");
        if (customMessage != null) {
            reward.message = customMessage;
        }

        reward.useMultiplier = section.getBoolean("multiply-by-difficulty");
        return reward;
    }

    public void rewardPlayer(Challenge challenge, Player player) {
        HPPlayer.getHPPlayer(player.getUniqueId()).addXp(useMultiplier ? xp * challenge.getDifficulty() : xp);
    }

    public String getRewardString(Player player) {
        if (message != null) return message;
        return getDefaultRewardString(player);
    }

    public boolean isUsingMultiplier() {
        return useMultiplier;
    }

    public abstract String getDefaultRewardString(Player player);

    public void multiplyRewardValues(int multiplier) {}

    public long getXp() {
        return xp;
    }
}
