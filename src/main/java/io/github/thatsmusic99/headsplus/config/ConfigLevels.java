package io.github.thatsmusic99.headsplus.config;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.BaseLevel;
import io.github.thatsmusic99.headsplus.api.Level;
import io.github.thatsmusic99.headsplus.api.Reward;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import io.github.thatsmusic99.headsplus.util.HPUtils;

import java.util.HashMap;

public class ConfigLevels extends FeatureConfig {

    private final HashMap<Integer, BaseLevel> levels = new HashMap<>();

    public HashMap<Integer, BaseLevel> getDefLevels() {
        return levels;
    }

    private int maxHierarchy = 0;
    private static ConfigLevels instance;

    public ConfigLevels() {
        super("levels.yml");
        instance = this;
        addDefLevels();
    }

    public static ConfigLevels get() {
        return instance;
    }

    @Override
    public void postSave() {
        HeadsPlus hp = HeadsPlus.get();
        hp.getLevels().clear();
        //if (hp.usingLevels()) {
            try {
                for (String s : getConfigSection("levels").getKeys(false)) {
                    String dn = HPUtils.notNull(getString("levels." + s + ".display-name"), "There is no display name for level " + s + "!");
                    double av = getDouble("levels." + s + ".added-version");
                    int rxp = getInteger("levels." + s + ".required-xp");
                    int h = getInteger("levels." + s + ".hierarchy");
                    boolean e = getBoolean("levels." + s + ".rewards.enabled", false);
                    HPChallengeRewardTypes reward;
                    try {

                        reward = HPChallengeRewardTypes.valueOf(HPUtils.notNull(getString("levels." + s + ".rewards.reward-type"), "There is no reward type for " + s + "!").toUpperCase());
                    } catch (Exception ex) {
                        throw new NullPointerException("The reward type for " + s + " is not valid!");
                    }
                    Object rewardVal = HPUtils.notNull(get("levels." + s + ".rewards.reward-value"), "The reward value for level " + s + " is null!");
                    int items = getInteger("levels." + s + ".rewards.item-amount");
                    String sender = getString("levels." + s + ".rewards.command-sender");
                    Reward reward1 = new Reward("", reward, rewardVal, items, sender, 0, false);
                    Level c = new Level(s, dn, rxp, av, e, reward1);
                    hp.getLevels().put(h, c);
                    if (h > maxHierarchy) {
                        maxHierarchy = h;
                    }
                }
            } catch (NullPointerException ex) {
                hp.getLogger().warning(ex.getMessage());
            }
        //}
    }

    @Override
    public void loadDefaults() {
        double version = 0.3;
        double current = getDouble("version");
        if (isNew() || current < version) {
            set("version", version);
            for (int i = 1; i <= getDefLevels().size(); i++) {
                BaseLevel l = getDefLevels().get(i);
                if (current < 0.3) {
                    set("levels." + l.getConfigName() + ".hierarchy", i);
                    set("levels." + l.getConfigName() + ".hierachy", null);
                }
                if (l.getAddedVersion() > current) {
                    addExample("levels." + l.getConfigName() + ".display-name", l.getDisplayName());
                    addExample("levels." + l.getConfigName() + ".added-version", l.getAddedVersion());
                    addExample("levels." + l.getConfigName() + ".required-xp", l.getRequiredXP());
                    addExample("levels." + l.getConfigName() + ".hierarchy", i);
                    addDefault("levels." + l.getConfigName() + ".rewards.enabled", false);
                    addDefault("levels." + l.getConfigName() + ".rewards.reward-type", HPChallengeRewardTypes.ECO.name());
                    addDefault("levels." + l.getConfigName() + ".rewards.reward-value", 300);
                    addDefault("levels." + l.getConfigName() + ".rewards.item-amount", 0);
                    addDefault("levels." + l.getConfigName() + ".rewards.command-sender", "player");
                }

            }
        }
    }

    private void addDefLevels() {
        levels.clear();
        levels.put(1, new BaseLevel("grass", "&2&lGrass", 0, 0.1));
        levels.put(2, new BaseLevel("dirt", "&6&lDirt", 250, 0.1));
        levels.put(3, new BaseLevel("stone", "&7&lStone", 750, 0.1));
        levels.put(4, new BaseLevel("coal", "&8&lCoal", 1500, 0.1));
        levels.put(5,  new BaseLevel("coal_2", "&8&lCoal &0&lII", 2000, 0.1));
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

    public int getMaxHierarchy() {
        return maxHierarchy;
    }

    @Override
    public boolean shouldLoad() {
        return MainConfig.get().getMainFeatures().LEVELS;
    }
}
