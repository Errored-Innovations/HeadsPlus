package io.github.thatsmusic99.headsplus.config.levels;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.CLevel;
import io.github.thatsmusic99.headsplus.api.Level;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class HeadsPlusLevels extends ConfigSettings {

    private HashMap<Integer, Level> levels = new HashMap<>();

    public HashMap<Integer, Level> getDefLevels() {
        return levels;
    }

    private double version;

    public HeadsPlusLevels() {
        this.conName = "levels";
        enable(false);

    }

    @Override
    public void reloadC(boolean a) {
        addDefLevels();
        boolean n = false;
        if (configF == null || !configF.exists()) {
            n = true;
            configF = new File(HeadsPlus.getInstance().getDataFolder(), "levels.yml");
        }
        config = YamlConfiguration.loadConfiguration(configF);
        if (n) {
            load(false);
        }
        getConfig().options().copyDefaults(true);
        save();
        loadLevels();
    }

    private void loadLevels() {
        HeadsPlus hp = HeadsPlus.getInstance();
        hp.getLevels().clear();
        if (hp.usingLevels()) {
            for (String s : getConfig().getConfigurationSection("levels").getKeys(false)) {
                String dn = getConfig().getString("levels." + s + ".display-name");
                double av = getConfig().getDouble("levels." + s + ".added-version");
                int rxp = getConfig().getInt("levels." + s + ".required-xp");
                int h = getConfig().getInt("levels." + s + ".hierachy");
                boolean e = getConfig().getBoolean("levels." + s + ".rewards.enabled", false);
                HPChallengeRewardTypes reward;
                try {

                    reward = HPChallengeRewardTypes.valueOf(getConfig().getString("levels." + s + ".rewards.reward-type").toUpperCase());
                } catch (Exception ex) {
                    continue;
                }
                Object rewardVal = getConfig().get("levels." + s + ".rewards.reward-value");
                int items = getConfig().getInt("levels." + s + ".rewards.item-amount");
                String sender = getConfig().getString("levels." + s + ".rewards.command-sender");
                CLevel c = new CLevel(s, dn, rxp, av, e, reward, rewardVal, items, sender);
                hp.getLevels().put(h, c);
            }
        }
    }

    @Override
    public void load(boolean n) {
        for (int i = 1; i <= getDefLevels().size(); i++) {
            Level l = getDefLevels().get(i);
            getConfig().addDefault("version", 0.0);
           // if (getConfig().getDouble("version") < version) {
            getConfig().addDefault("levels." + l.getConfigName() + ".display-name", l.getDisplayName());
            getConfig().addDefault("levels." + l.getConfigName() + ".added-version", l.getAddedVersion());
            getConfig().addDefault("levels." + l.getConfigName() + ".required-xp", l.getRequiredXP());
            getConfig().addDefault("levels." + l.getConfigName() + ".hierachy", i);
            getConfig().addDefault("levels." + l.getConfigName() + ".rewards.enabled", false);
            getConfig().addDefault("levels." + l.getConfigName() + ".rewards.reward-type", HPChallengeRewardTypes.ECO.name());
            getConfig().addDefault("levels." + l.getConfigName() + ".rewards.reward-value", 300);
            getConfig().addDefault("levels." + l.getConfigName() + ".rewards.item-amount", 0);
            getConfig().addDefault("levels." + l.getConfigName() + ".rewards.command-sender", "player");
          //  }
        }
        save();
    }

    private void addDefLevels() {
        levels.clear();
        levels.put(1, new StarterLevels.Grass());
        levels.put(2, new StarterLevels.Dirt());
        levels.put(3, new StarterLevels.Stone());
        levels.put(4, new LowerLevels.Coal());
        levels.put(5, new LowerLevels.CoalII());
        levels.put(6, new LowerLevels.Iron());
        levels.put(7, new LowerLevels.IronII());
        levels.put(8, new LowerLevels.Redstone());
        levels.put(9, new LowerLevels.RedstoneII());
        levels.put(10, new MidLevels.Lapis());
        levels.put(11, new MidLevels.LapisII());
        levels.put(12, new MidLevels.LapisIII());
        levels.put(13, new MidLevels.Gold());
        levels.put(14, new MidLevels.GoldII());
        levels.put(15, new MidLevels.GoldIII());
        levels.put(16, new MidLevels.Diamond());
        levels.put(17, new MidLevels.DiamondII());
        levels.put(18, new MidLevels.DiamondIII());
        levels.put(19, new HigherLevels.Obsidian());
        levels.put(20, new HigherLevels.ObsidianII());
        levels.put(21, new HigherLevels.ObsidianIII());
        levels.put(22, new HigherLevels.ObsidianIV());
        levels.put(23, new HigherLevels.Emerald());
        levels.put(24, new HigherLevels.EmeraldII());
        levels.put(25, new HigherLevels.EmeraldIII());
        levels.put(26, new HigherLevels.EmeraldIV());
        levels.put(27, new HigherLevels.Bedrock());
        levels.put(28, new HigherLevels.BedrockII());
        levels.put(29, new HigherLevels.BedrockIII());
        levels.put(30, new HigherLevels.BedrockIV());
        levels.put(31, new HigherLevels.BedrockV());
    }




}
