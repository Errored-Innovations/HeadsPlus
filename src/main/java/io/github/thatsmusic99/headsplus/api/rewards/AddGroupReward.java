package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AddGroupReward extends Reward<String> {

    public AddGroupReward(@NotNull String group, long xp) {
        super(group, xp);
    }

    public static AddGroupReward fromConfigSection(String id, ConfigSection section) {
        if (!section.contains("base-value") && !section.contains("reward-value"))
            throw new IllegalStateException("Reward type ADD_GROUP for reward " + id + " must have a base-value " +
                    "option!");
        return new AddGroupReward(section.getString("base-value", section.getString("reward-value")),
                section.getLong("base-xp", 0));
    }

    @Override
    public void rewardPlayer(Challenge challenge, @NotNull Player player) {
        super.rewardPlayer(challenge, player);
        Permission permissions = HeadsPlus.get().getPermissions();
        if (!permissions.isEnabled()) return;
        if (permissions.playerInGroup(player, this.reward)) return;
        permissions.playerAddGroup(player, this.reward);
    }

    @Override
    public String getDefaultRewardString(Player player, int difficulty) {
        return MessagesManager.get().getString("inventory.icon.reward.group-add", player)
                .replace("{group}", this.reward);
    }
}
