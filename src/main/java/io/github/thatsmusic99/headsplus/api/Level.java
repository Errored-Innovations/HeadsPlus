package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class Level extends BaseLevel {

    private final String configName;
    private final String displayName;
    private final int requiredXP;
    private final double addedVersion;
    private final boolean rEnabled;
    private final Reward reward;

    public Level(String configName, String displayName, int requiredXP, double addedVersion, boolean e, Reward reward) {
        super(configName, displayName, requiredXP, addedVersion);
        this.configName = configName;
        this.displayName = displayName;
        this.requiredXP = requiredXP;
        this.addedVersion = addedVersion;
        rEnabled = e;
        this.reward = reward;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getRequiredXP() {
        return requiredXP;
    }

    @Override
    public double getAddedVersion() {
        return addedVersion;
    }

    public boolean isrEnabled() {
        return rEnabled;
    }

    public Reward getReward() {
        return reward;
    }
}
