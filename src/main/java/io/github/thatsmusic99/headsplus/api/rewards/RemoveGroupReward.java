package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

public class RemoveGroupReward extends Reward {

    private final String group;

    public RemoveGroupReward(String group, int xp) {
        super(xp);
        this.group = group;
    }

    public static RemoveGroupReward fromConfigSection(String id, ConfigSection section) {
        if (!section.contains("base-value") && !section.contains("reward-value"))
            throw new IllegalStateException("Reward type REMOVE_GROUP for reward " + id + " must have a base-value option!");
        return new RemoveGroupReward(section.getString("base-value", section.getString("reward-value")), 
                section.getInteger("base-xp"));
    }

    @Override
    public void rewardPlayer(Challenge challenge, Player player) {
        super.rewardPlayer(challenge, player);
        Permission permissions = HeadsPlus.get().getPermissions();
        if (!permissions.isEnabled()) return;
        if (!permissions.playerInGroup(player, group)) return;
        permissions.playerRemoveGroup(player, group);
    }

    @Override
    public String getDefaultRewardString(Player player) {
        return MessagesManager.get().getString("inventory.icon.reward.group-remove", player)
                .replace("{group}", group);
    }
}
