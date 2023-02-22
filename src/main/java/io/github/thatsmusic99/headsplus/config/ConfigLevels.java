package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.BaseLevel;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;

import java.io.IOException;
import java.util.HashMap;

public class ConfigLevels extends FeatureConfig {

    private final HashMap<Integer, BaseLevel> levels = new HashMap<>();

    public HashMap<Integer, BaseLevel> getDefLevels() {
        return levels;
    }

    private static ConfigLevels instance;

    public ConfigLevels() throws IOException, IllegalAccessException {
        super("levels.yml");
        instance = this;
        addDefLevels();
    }

    public static ConfigLevels get() {
        return instance;
    }

    @Override
    public void loadDefaults() {
        double version = 0.3;
        double current = getDouble("version");
        makeSectionLenient("levels");
        if (current < version) {
            set("version", version);
            for (int i = 1; i <= getDefLevels().size(); i++) {
                BaseLevel l = getDefLevels().get(i);
                if (l.getAddedVersion() > current) {
                    addExample("levels." + l.getConfigName() + ".display-name", l.getDisplayName());
                    addExample("levels." + l.getConfigName() + ".added-version", l.getAddedVersion());
                    addExample("levels." + l.getConfigName() + ".required-xp", l.getRequiredXP());
                    addExample("levels." + l.getConfigName() + ".hierarchy", i);
                    addExample("levels." + l.getConfigName() + ".rewards.enabled", false);
                    addExample("levels." + l.getConfigName() + ".rewards.reward-type",
                            HPChallengeRewardTypes.ECO.name());
                    addExample("levels." + l.getConfigName() + ".rewards.reward-value", 300);
                    addExample("levels." + l.getConfigName() + ".rewards.item-amount", 0);
                    addExample("levels." + l.getConfigName() + ".rewards.command-sender", "player");
                }

            }
        }
    }

    @Override
    public void moveToNew() {

        ConfigSection levelsSection = getConfigSection("levels");
        if (levelsSection == null) return;

        for (String key : levelsSection.getKeys(false)) {
            moveTo("levels." + key + ".hierachy", "levels." + key + ".hierarchy");
        }
    }

    private void addDefLevels() {
        levels.clear();
        levels.put(1, new BaseLevel("grass", "&2&lGrass", 0, 0.1));
        levels.put(2, new BaseLevel("dirt", "&6&lDirt", 250, 0.1));
        levels.put(3, new BaseLevel("stone", "&7&lStone", 750, 0.1));
        levels.put(4, new BaseLevel("coal", "&8&lCoal", 1500, 0.1));
        levels.put(5, new BaseLevel("coal_2", "&8&lCoal &0&lII", 2000, 0.1));
        levels.put(6, new BaseLevel("iron", "&f&lIron", 2750, 0.1));
        levels.put(7, new BaseLevel("iron_2", "&f&lIron &7&lII", 3500, 0.1));
        levels.put(8, new BaseLevel("redstone", "&c&lRedstone", 4500, 0.1));
        levels.put(9, new BaseLevel("redstone_2", "&c&lRedstone &4&lII", 5500, 0.1));
        levels.put(10, new BaseLevel("lapis", "&9&lLapis Lazuli", 7000, 0.1));
        levels.put(11, new BaseLevel("lapis_2", "&9&lLapis Lazuli &1&lII", 8000, 0.1));
        levels.put(12, new BaseLevel("lapis_3", "&9&lLapis Lazuli &1&lIII", 9000, 0.1));
        levels.put(13, new BaseLevel("gold", "&e&lGold", 10000, 0.1));
        levels.put(14, new BaseLevel("gold_2", "&e&lGold &6&lII", 12500, 0.1));
        levels.put(15, new BaseLevel("gold_3", "&e&lGold &6&lIII", 15000, 0.1));
        levels.put(16, new BaseLevel("diamond", "&b&lDiamond", 17000, 0.1));
        levels.put(17, new BaseLevel("diamond_2", "&b&lDiamond &3&lII", 20000, 0.1));
        levels.put(18, new BaseLevel("diamond_3", "&b&lDiamond &3&lIII", 22500, 0.1));
        levels.put(19, new BaseLevel("obsidian", "&8&lObsidian", 25000, 0.1));
        levels.put(20, new BaseLevel("obsidian_2", "&8&lObsidian &0&lII", 30000, 0.1));
        levels.put(21, new BaseLevel("obsidian_3", "&8&lObsidian &0&lIII", 35000, 0.1));
        levels.put(22, new BaseLevel("obsidian_4", "&8&lObsidian &0&lIV", 40000, 0.1));
        levels.put(23, new BaseLevel("emerald", "&a&lEmerald", 45000, 0.1));
        levels.put(24, new BaseLevel("emerald_2", "&a&lEmerald &2&lII", 50000, 0.1));
        levels.put(25, new BaseLevel("emerald_3", "&a&lEmerald &2&lIII", 60000, 0.1));
        levels.put(26, new BaseLevel("emerald_4", "&a&lEmerald &2&lIV", 75000, 0.1));
        levels.put(27, new BaseLevel("bedrock", "&5&lBedrock", 100000, 0.1));
        levels.put(28, new BaseLevel("bedrock_2", "&5&lBedrock &0&lII", 125000, 0.1));
        levels.put(29, new BaseLevel("bedrock_3", "&5&lBedrock &0&lIII", 150000, 0.1));
        levels.put(30, new BaseLevel("bedrock_4", "&5&lBedrock &0&lIV", 200000, 0.1));
        levels.put(31, new BaseLevel("bedrock_5", "&5&lBedrock &0&lV", 300000, 0.1));
        levels.put(32, new BaseLevel("netherite", "&4&lNetherite I", 500000, 0.2));
        levels.put(33, new BaseLevel("netherite_2", "&4&lNetherite II", 750000, 0.2));
        levels.put(34, new BaseLevel("netherite_3", "&4&lNetherite III", 1000000, 0.2));
        levels.put(35, new BaseLevel("netherite_4", "&4&lNetherite IV", 1500000, 0.2));
        levels.put(36, new BaseLevel("netherite_5", "&4&lNetherite V", 2000000, 0.2));
    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().LEVELS;
    }
}
