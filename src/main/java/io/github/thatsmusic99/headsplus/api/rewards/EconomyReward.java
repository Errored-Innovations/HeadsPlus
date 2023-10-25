package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EconomyReward extends Reward<Double> {

    public EconomyReward(double money, long xp) {
        super(money, xp);
    }

    public static EconomyReward fromConfigSection(String id, ConfigSection section) {
        if (!section.contains("base-value") && !section.contains("reward-value"))
            throw new IllegalStateException("Reward type ECO for reward " + id + " must have a base-value option!");
        return new EconomyReward(section.getDouble("base-value", section.getDouble("reward-value")),
                section.getLong("base-xp", 0));
    }

    @Override
    public void rewardPlayer(@Nullable Challenge challenge, @NotNull Player player) {
        super.rewardPlayer(challenge, player);
        if (!HeadsPlus.get().isVaultEnabled()) return;
        if (!HeadsPlus.get().getEconomy().isEnabled()) return;
        int difficulty = 1;
        if (isUsingMultiplier() && challenge != null) {
            difficulty = challenge.getDifficulty();
        }
        HeadsPlus.get().getEconomy().depositPlayer(player, this.reward * difficulty);
    }

    @Override
    public String getDefaultRewardString(Player player, int difficulty) {
        return MessagesManager.get().getString("inventory.icon.reward.currency", player)
                .replace("{amount}", String.valueOf(multiplyRewardValues(difficulty)));
    }

    @Override
    public Double multiplyRewardValues(int multiplier) {
        return this.reward * multiplier;
    }
}
