package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

public class AddGroupReward extends Reward {

    private final String group;

    public AddGroupReward(String group, int xp) {
        super(xp);
        this.group = group;
    }

    public static AddGroupReward fromConfigSection(String id, ConfigSection section) {
        if (!section.contains("base-value"))
            throw new IllegalStateException("Reward type ADD_GROUP for reward " + id + " must have a base-value option!");
        return new AddGroupReward(section.getString("base-value"), section.getInteger("base-xp"));
    }

    @Override
    public void rewardPlayer(Challenge challenge, Player player) {
        super.rewardPlayer(challenge, player);
        Permission permissions = HeadsPlus.get().getPermissions();
        if (!permissions.isEnabled()) return;
        if (permissions.playerInGroup(player, group)) return;
        permissions.playerAddGroup(player, group);
    }

    @Override
    public String getDefaultRewardString(Player player) {
        return MessagesManager.get().getString("inventory.icon.reward.group-add", player)
                .replace("{group}", group);
    }
}
