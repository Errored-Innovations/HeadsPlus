package io.github.thatsmusic99.headsplus.api.rewards;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;

public class RemoveGroupReward extends Reward {

    private String group;

    public RemoveGroupReward(String group, int xp) {
        super(xp);
        this.group = group;
    }

    public static RemoveGroupReward fromConfigSection(String id, ConfigSection section) {
        return null;
    }

    @Override
    public void rewardPlayer(Player player) {
        super.rewardPlayer(player);
        Permission permissions = HeadsPlus.get().getPermissions();
        if (!permissions.isEnabled()) return;
        if (!permissions.playerInGroup(player, group)) return;
        permissions.playerRemoveGroup(player, group);
    }

    @Override
    public String getDefaultRewardString(Player player) {
        return HeadsPlusMessagesManager.get().getString("inventory.icon.reward.group-remove", player)
                .replace("{group}", group);
    }
}
