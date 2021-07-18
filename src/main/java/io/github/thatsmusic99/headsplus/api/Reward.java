package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.rewards.*;
import org.bukkit.entity.Player;

public abstract class Reward {

    private int xp;
    private String message;

    public Reward(int xp) {
        this.xp = xp;
    }

    public static Reward fromConfigSection(String id, ConfigSection section) {
        String type = section.getString("type");
        if (type == null) throw new IllegalStateException("There is no reward type for " + id + "!");
        switch (type.toLowerCase()) {
            case "eco":
                return EconomyReward.fromConfigSection(id, section);
            case "give_item":
                return ItemReward.fromConfigSection(id, section);
            case "add_group":
                return AddGroupReward.fromConfigSection(id, section);
            case "remove_group":
                return RemoveGroupReward.fromConfigSection(id, section);
            case "run_command":
                return RunCommandReward.fromConfigSection(id, section);
            default:
                throw new IllegalStateException("No such reward type " + type + " for " + id + "!");

        }
    }

    public void rewardPlayer(Player player) {
        HPPlayer.getHPPlayer(player).addXp(xp);
    }

    public String getRewardString(Player player) {
        if (message != null) return message;
        return getDefaultRewardString(player);
    }

    public abstract String getDefaultRewardString(Player player);

    public void multiplyRewardValues(int multiplier) {}

    public int getXp() {
        return xp;
    }
}
