package io.github.thatsmusic99.headsplus.config.challenges;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.config.ConfigSettings;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.List;

public class HeadsPlusChallenges extends ConfigSettings {

    public HeadsPlusChallenges() {
        this.conName = "challenges";
        reloadC(false);
    }

    @Override
    public void reloadC(boolean a) {
        if (configF == null || !configF.exists()) {
            configF = new File(HeadsPlus.getInstance().getDataFolder(), "challenges.yml");
        }
        config = YamlConfiguration.loadConfiguration(configF);
        if (configF.length() < 20) {
            load(false);
        }
        boolean b = getConfig().getBoolean("challenges.options.update-challenges");
        double v = getConfig().getDouble("challenges.options.current-version");
        if (v < 1.2 && b) {
            for (HeadsPlusChallengeEnums hpc : HeadsPlusChallengeEnums.values()) {
                if (hpc.v > v) {
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".name", hpc.dName);
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".header", hpc.h);
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".description", hpc.d);
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".type", hpc.p.name());
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".min", hpc.m);
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".reward-type", hpc.r.name());
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".reward-value", hpc.o instanceof Material ? ((Material) hpc.o).name() : hpc.o);
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".item-amount", hpc.a);
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".head-type", hpc.t);
                    getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".xp", hpc.exp);
                }

            }
            getConfig().set("challenges.options.current-version", 1.2);
            getConfig().options().copyDefaults(true);
        }

        save();
        addChallenges();
    }

    @Override
    public void load(boolean aaaan) {
        getConfig().options().header("HeadsPlus by Thatsmusic99 - Challenge configuration" +
                "\nKey for challenges:" +
                "\nHeader - what is displayed as the challenge title." +
                "\nDescription - Description for the challenge." +
                "\nType - what kind of challenge it is, valid values: SELLHEAD, CRAFTING AND LEADERBOARD" +
                "\nMin - The minimum amount of heads required to complete the challenge, if required." +
                "\nReward type - The type of reward at hand, valid values: ECO (to give money), ADD_GROUP (to give someone a new group), REMOVE_GROUP (to remove someone from a group because LOL), and GIVE_ITEM" +
                "\nReward value - What the reward is, for example: if Reward type is set to ECO, it would be for example 500, if ADD_GROUP, it would be the group name, etc." +
                "\nItem Amount - If using GIVE_ITEM as a reward type, you can set an it" +
                "\nHead type - The head type required to complete the challenge, use \"total\" for all types." +
                "\nXP - Amount of XP (HeadsPlus levels) that will be received.");
        getConfig().addDefault("challenges.options.current-version", 1.2);
        double v = getConfig().getDouble("challenges.options.current-version");
        if (v < 1.2) {
            getConfig().set("challenges.options.current-version", 1.2);
        }
        for (HeadsPlusChallengeEnums hpc : HeadsPlusChallengeEnums.values()) {
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".name", hpc.dName);
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".header", hpc.h);
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".description", hpc.d);
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".type", hpc.p.name());
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".min", hpc.m);
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".reward-type", hpc.r.name());
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".reward-value", hpc.o instanceof Material ? ((Material) hpc.o).name() : hpc.o);
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".item-amount", hpc.a);
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".head-type", hpc.t);
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".xp", hpc.exp);
            getConfig().addDefault("challenges." + hpc.cd.name() + "." + hpc.n + ".command-sender", "player");

        }
        if (!HeadsPlus.getInstance().isConnectedToMySQLDatabase()) {
            getConfig().addDefault("server-total.sellhead.total", 0);
            for (EntityType e : HeadsPlus.getInstance().getDeathEvents().ableEntities) {
                getConfig().addDefault("server-total.sellhead." + e.name(), 0);
            }
            getConfig().addDefault("server-total.crafting.total", 0);
            for (EntityType e : HeadsPlus.getInstance().getDeathEvents().ableEntities) {
                getConfig().addDefault("server-total.crafting." + e.name(), 0);
            }
        }
        getConfig().addDefault("challenges.options.update-challenges", true);
        getConfig().options().copyDefaults(true);
        save();
    }

    private void addChallenges() {
        HeadsPlus.getInstance().getChallenges().clear();
        for (String st : config.getConfigurationSection("challenges").getKeys(false)) {
            if (st.equalsIgnoreCase("current-version") || st.equalsIgnoreCase("options")) continue;
            for (String s : config.getConfigurationSection("challenges." + st).getKeys(false)) {
                String name = config.getString("challenges." + st + "." + s + ".name");
                String header = config.getString("challenges." + st + "." + s + ".header");
                List<String> desc = config.getStringList("challenges." + st + "." + s + ".description");
                HeadsPlusChallengeTypes type;
                try {
                    type = HeadsPlusChallengeTypes.valueOf(config.getString("challenges." + st + "." + s + ".type").toUpperCase());
                } catch (Exception ex) {
                    continue;
                }
                int min = config.getInt("challenges." + st + "." + s + ".min");
                HPChallengeRewardTypes reward;
                try {
                    reward = HPChallengeRewardTypes.valueOf(config.getString("challenges." + st + "." + s + ".reward-type").toUpperCase());
                } catch (Exception e) {
                    continue;
                }
                Object rewardVal = config.get("challenges." + st + "." + s + ".reward-value");
                int items = config.getInt("challenges." + st + "." + s + ".item-amount");
                String headType = config.getString("challenges." + st + "." + s + ".head-type");
                int xp = config.getInt("challenges." + st + "." + s + ".xp");
                String sender = config.getString("challenges." + st + "." + s + ".command-sender");
                String rewardString = config.getString("challenges." + st + "." + s + ".reward-string");
                Challenge c = new Challenge(s, name, header, desc, min, type, reward, rewardVal, items, headType, xp, HeadsPlusChallengeDifficulty.valueOf(st.toUpperCase()), sender, rewardString);
                HeadsPlus.getInstance().getChallenges().add(c);

            }
        }

    }
}
