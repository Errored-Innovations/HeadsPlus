package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.Level;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.ConfigLevels;
import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class LevelsManager {

    private final HashMap<String, Level> levels;
    private final List<String> levelOrder;
    private static LevelsManager instance;

    public LevelsManager() {
        instance = this;
        levels = new HashMap<>();
        levelOrder = new ArrayList<>();
        init();
    }

    public static LevelsManager get() {
        return instance;
    }

    public void reload() {
        levels.clear();
        levelOrder.clear();
        init();
    }

    public Level getLevel(String key) {
        return levels.get(key);
    }

    public @Nullable Level getLevel(int position) {
        return levelOrder.isEmpty() ? null : getLevel(levelOrder.get(position));
    }

    public @Nullable Level getNextLevel(String key) {
        int order = levelOrder.indexOf(key) + 1;
        if (order == levelOrder.size()) return null;
        return getLevel(order);
    }

    public Level getLevelFromXp(long xp) {
        int index = 0;
        for (String name : levelOrder) {
            if (levels.get(name).getRequiredXP() <= xp) {
                continue;
            }
            index = levelOrder.indexOf(name);
            break;
        }
        return index > 0 ? getLevel(index - 1) : getLevel(0);
    }

    public List<String> getLevels() {
        return levelOrder;
    }

    private void init() {
        ConfigLevels levelsConfig = ConfigLevels.get();
        ConfigSection levelsSection = levelsConfig.getConfigSection("levels");
        if (levelsSection == null) return;
        for (String levelKey : levelsSection.getKeys(false)) {
            try {
                ConfigSection levelSection = levelsSection.getConfigSection(levelKey);
                // If the section is null, just continue
                if (levelSection == null) continue;
                // Get the display name
                String displayName = Objects.requireNonNull(levelSection.getString("display-name"), "There is no " +
						"display name set for level " + levelKey + "!");
                // If there's no required XP, tell the user "wtf"
                if (!levelSection.contains("required-xp")) {
                    throw new NullPointerException("There is no required XP set for level " + levelKey + "!");
                }
                int xp = levelSection.getInteger("required-xp");
                // Get the reward
                String rewardKey = "levels_" + levelKey;
                Reward reward;
                if (!(levelSection.get("rewards") instanceof ConfigSection)) {
                    rewardKey = levelSection.getString("rewards");
                    reward = RewardsManager.get().getReward(rewardKey);
                } else if (!levelSection.getBoolean("rewards.enabled")) {
                    reward = null;
                } else {
                    reward = RewardsManager.get().getReward(rewardKey);
                }
                Level level = new Level(levelKey, displayName, xp, reward);
                // Now set the order stuff properly
                int hierarchy = levelSection.getInteger("hierarchy");
                if (hierarchy < 1) {
                    HeadsPlus.get().getLogger().warning("Cannot have a hierarchy below 1 for level " + levelKey + "!");
                    return;
                }
                levelOrder.add(hierarchy - 1, levelKey);
                levels.put(levelKey, level);
            } catch (NullPointerException ex) {
                HeadsPlus.get().getLogger().warning("Null value received when registering level " + levelKey + ": " + ex.getMessage());
            }
        }
        HeadsPlus.get().getLogger().info("Registered " + levels.size() + " levels.");
    }
}
