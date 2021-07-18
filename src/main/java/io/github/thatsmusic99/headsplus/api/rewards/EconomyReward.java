package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.entity.Player;

public class EconomyReward extends Reward {

    private double money;

    public EconomyReward(double money, int xp) {
        super(xp);
        this.money = money;
    }

    public static EconomyReward fromConfigSection(String id, ConfigSection section) {
        if (!section.contains("base-value"))
            throw new IllegalStateException("Reward type ECO for reward " + id + " must have a base-value option!");
        return new EconomyReward(section.getDouble("base-value"), section.getInteger("base-xp"));
    }

    @Override
    public void rewardPlayer(Player player) {
        if (!HeadsPlus.get().isVaultEnabled()) return;
        if (!HeadsPlus.get().getEconomy().isEnabled()) return;
        HeadsPlus.get().getEconomy().depositPlayer(player, money);
    }

    @Override
    public String getDefaultRewardString(Player player) {
        return HeadsPlusMessagesManager.get().getString("inventory.icon.reward.currency", player)
                .replace("{amount}", String.valueOf(money));
    }
}
