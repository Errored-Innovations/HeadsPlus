package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.rewards.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Reward<T> {

    protected @NotNull T reward;
    private @Nullable String message;
    private final long xp;
    private boolean useMultiplier;

    public Reward(@NotNull T reward, long xp) {
        this.xp = xp;
        this.reward = reward;
    }

    public static Reward<?> fromConfigSection(String id, ConfigSection section) {
        String type = section.getString("type");
        if (type == null) type = section.getString("reward-type");
        if (type == null)
            throw new IllegalStateException("There is no reward type for " + id + "!");

        Reward<?> reward;
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

    public void rewardPlayer(@Nullable Challenge challenge, @NotNull Player player) {
        HPPlayer.getHPPlayer(player.getUniqueId())
                .addXp(useMultiplier ? xp * (challenge == null ? 1 : challenge.getDifficulty()) : xp);
    }

    public String getRewardString(Player player) {
        return getRewardString(player, 1);
    }

    public String getRewardString(Player player, int difficulty) {
        if (message != null) return message;
        return getDefaultRewardString(player, isUsingMultiplier() ? difficulty : 1);
    }

    public boolean isUsingMultiplier() {
        return useMultiplier;
    }

    public abstract String getDefaultRewardString(Player player, int difficulty);

    public T multiplyRewardValues(int multiplier) {
        return this.reward;
    }

    public long getXp() {
        return getXp(1);
    }

    public long getXp(int multiplier) {
        return useMultiplier ? multiplier * xp : xp;
    }
}
