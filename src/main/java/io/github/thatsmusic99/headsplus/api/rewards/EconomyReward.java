package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import org.bukkit.entity.Player;

public class EconomyReward extends Reward {

    private final double money;

    public EconomyReward(double money, int xp) {
        super(xp);
        this.money = money;
    }

    public static EconomyReward fromConfigSection(String id, ConfigSection section) {
        if (!section.contains("base-value") && !section.contains("reward-value"))
            throw new IllegalStateException("Reward type ECO for reward " + id + " must have a base-value option!");
        return new EconomyReward(section.getDouble("base-value", section.getDouble("reward-value")),
                section.getInteger("base-xp", 0));
    }

    @Override
    public void rewardPlayer(Challenge challenge, Player player) {
        super.rewardPlayer(challenge, player);
        if (!HeadsPlus.get().isVaultEnabled()) return;
        if (!HeadsPlus.get().getEconomy().isEnabled()) return;
        int difficulty = 1;
        if (isUsingMultiplier()) {
            difficulty = challenge.getDifficulty();
        }
        HeadsPlus.get().getEconomy().depositPlayer(player, money * difficulty);
    }

    @Override
    public String getDefaultRewardString(Player player) {
        return MessagesManager.get().getString("inventory.icon.reward.currency", player)
                .replace("{amount}", String.valueOf(money));
    }
}
