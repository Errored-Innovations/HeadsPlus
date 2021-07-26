package io.github.thatsmusic99.headsplus.managers;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.Level;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.ConfigLevels;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class LevelsManager {

    private final HashMap<String, Level> levels;
    private final List<String> levelOrder;
    private static LevelsManager instance;

    public LevelsManager() {
	instance = this;
        levels = new HashMap<>();
	levelOrder = new ArrayList<>();
    }

    public static LevelsManager get() {
        return instance;
    }

    public Level getLevel(String key) {
        return levels.get(key);
    }

    public Level getLevel(int position) {
        return getLevel(levelOrder.get(position));
    }

    public Level getNextLevel(String key) {
        int order = levelOrder.indexOf(key) + 1;
	if (order == levelOrder.size()) return null;
	return levelOrder.get(order);
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
	        String displayName = Objects.requireNonNull(levelSection.getString("display-name"), "There is no display name set for level " + levelKey + "!");
	        // If there's no required XP, tell the user "wtf"
		if (!levelSection.contains("required-xp")) {
		    throw new NullPointerException("There is no required XP set for level " + levelKey + "!");
		}
		int xp = levelSection.getInteger("required-xp");
		// Get the reward
		String rewardKey = "levels_" + levelKey;
		if (!(levelSection.get("rewards") instanceof ConfigSection)) {
		    rewardKey = levelSection.getString("rewards");
		}
		Reward reward = Reward.getReward(rewardKey);
		Level level = new Level(levelKey, displayName, xp, 0.0, reward != null, reward);
		// Now set the order stuff properly
		int hierarchy = levelSection.getInteger("hierarchy");
		if (hierarchy == 0) return; // TODO do properly
		levelOrder.add(hierarchy - 1, levelKey);
	       	levels.put(levelKey, level);
	    } catch (NullPointerException ex) {
                HeadsPlus.get().getLogger().warning(ex.getMessage());
	    }
	}
    }
}
