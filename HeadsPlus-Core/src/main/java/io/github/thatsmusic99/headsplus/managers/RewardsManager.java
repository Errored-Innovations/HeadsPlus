package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.headsplus.config.ConfigLevels;
import io.github.thatsmusic99.headsplus.config.challenges.ConfigChallenges;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Reward;

import java.util.HashMap;

public class RewardsManager {

    private static RewardsManager instance;
    private final HashMap<String, Reward> rewards;

    public RewardsManager() {
        instance = this;
        rewards = new HashMap<>();
        init();
    }

    public static RewardsManager get() {
        return instance;
    }

    public void reload() {
        rewards.clear();
        init();
    }

    public Reward getReward(String key) {
        return rewards.get(key);
    }

    public boolean contains(String key) {
        return rewards.containsKey(key);
    }

    public void init() {
        ConfigChallenges challenges = ConfigChallenges.get();
        ConfigSection rewards = challenges.getConfigSection("rewards");
        // If challenges are enabled and use rewards
        if (rewards != null) {
            for (String key : rewards.getKeys(false)) {
                try {
                    ConfigSection rewardSection = rewards.getConfigSection(key);
                    if (rewardSection == null) continue;
                    Reward reward = Reward.fromConfigSection(key, rewardSection);
                    this.rewards.put("challenges_" + key, reward);
                } catch (IllegalStateException ex) {
                    HeadsPlus.get().getLogger().warning(ex.getMessage());
                }
            }
        }
        ConfigLevels levels = ConfigLevels.get();
        rewards = levels.getConfigSection("rewards");
        // If levels are enabled and use rewards
        if (rewards != null) {
            for (String key : rewards.getKeys(false)) {
                try {
                    ConfigSection rewardSection = rewards.getConfigSection(key);
                    if (rewardSection == null) continue;
                    if (!rewardSection.getBoolean("enabled", true)) continue;
                    Reward reward = Reward.fromConfigSection(key, rewardSection);
                    this.rewards.put("levels_" + key, reward);
                } catch (IllegalStateException ex) {
                    HeadsPlus.get().getLogger().warning(ex.getMessage());
                }
            }
        }
        // Use inbuilt level rewards
        rewards = levels.getConfigSection("levels");
        if (rewards != null) {
            for (String key : rewards.getKeys(false)) {
                try {
                    ConfigSection levelSection = rewards.getConfigSection(key);
                    if (levelSection == null) continue;
                    ConfigSection rewardSection = levelSection.getConfigSection("rewards");
                    if (rewardSection == null) continue;
                    if (!rewardSection.getBoolean("enabled", true)) continue;
                    Reward reward = Reward.fromConfigSection(key, rewardSection);
                    this.rewards.put("levels_" + key, reward);
                } catch (IllegalStateException ex) {
                    HeadsPlus.get().getLogger().warning(ex.getMessage());
                }
            }
        }
    }
}
