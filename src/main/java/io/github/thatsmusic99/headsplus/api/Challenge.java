package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import io.github.thatsmusic99.headsplus.config.challenges.HeadsPlusChallengeTypes;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

public class Challenge {

    private final String configName;
    private final String mainName;
    private final String header;
    private final List<String> description;
    private final int requiredHeadAmount;
    private final HeadsPlusChallengeTypes challengeType;
    private final Reward reward;
    private final int difficulty;
    private final ItemStack icon;
    private final ItemStack completeIcon;
    private final String headType;

    public Challenge(String configName, String mainName, String header, List<String> description, int requiredHeadAmount, HeadsPlusChallengeTypes challengeType, String headType, Reward reward, int difficulty, ItemStack icon, ItemStack completeIcon) {
        this.configName = configName;
        this.mainName = mainName;
        this.header = header;
        this.description = description;
        this.requiredHeadAmount = requiredHeadAmount;
        this.challengeType = challengeType;
        this.headType = headType;
        this.reward = reward;
        if (reward.isMultiply() && reward.getType() == HPChallengeRewardTypes.ECO) {
            reward.setMoney(reward.getMoney() * difficulty);
            reward.setXp(reward.getXp() * difficulty);
        }
        this.difficulty = difficulty;
        this.icon = icon;
        this.completeIcon = completeIcon;
    }

    public String getConfigName() {
        return configName;
    }

    public HeadsPlusChallengeTypes getChallengeType() {
        return challengeType;
    }

    public int getRequiredHeadAmount() {
        return requiredHeadAmount;
    }

    public HPChallengeRewardTypes getRewardType() {
        return reward.getType();
    }

    public int getRewardItemAmount() {
        return reward.getItem().getAmount();
    }

    public List<String> getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public ItemStack getCompleteIcon() {
        return completeIcon;
    }

    public Object getRewardValue() {
        switch (reward.getType()) {
            case GIVE_ITEM:
                return reward.getItem();
            case RUN_COMMAND:
                return reward.getCommands();
            case ADD_GROUP:
            case REMOVE_GROUP:
                return reward.getGroup();
            case ECO:
                return reward.getMoney();
        }
        return null;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getChallengeHeader() {
        return header;
    }

    public String getMainName() {
        return mainName;
    }

    public Reward getReward() {
        return reward;
    }

    public String getHeadType() {
        return headType;
    }

    public int getGainedXP() {
        return reward.getXp();
    }

    public boolean canComplete(Player p) throws SQLException {
        if (getChallengeType() == HeadsPlusChallengeTypes.MISC) {
            return true;
        } else if (getChallengeType() == HeadsPlusChallengeTypes.CRAFTING) {
            return HeadsPlusAPI.getPlayerInLeaderboards(p, getHeadType().equals("total") ? "total" : getHeadType(), "headspluscraft") >= getRequiredHeadAmount();
        } else if (getChallengeType() == HeadsPlusChallengeTypes.LEADERBOARD) {
            return HeadsPlusAPI.getPlayerInLeaderboards(p, getHeadType().equals("total") ? "total" : getHeadType(), "headspluslb") >= getRequiredHeadAmount();
        } else {
            return HeadsPlusAPI.getPlayerInLeaderboards(p, getHeadType().equals("total") ? "total" : getHeadType(), "headsplussh") >= getRequiredHeadAmount();
        }
    }

    public boolean isComplete(Player p) {
        return HeadsPlus.get().getScores().getCompletedChallenges(p.getUniqueId().toString()).contains(getConfigName());
    }

    public void complete(Player p) {
        HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();
        HPPlayer player = HPPlayer.getHPPlayer(p);
        player.addCompleteChallenge(this);

        StringBuilder sb2 = new StringBuilder();
        HPChallengeRewardTypes re = reward.getType();
        if (re != HPChallengeRewardTypes.RUN_COMMAND) {
            String value = String.valueOf(getRewardValue());
            String rewardString = reward.getRewardString();
            if (rewardString != null) {
                sb2.append(rewardString);
            } else if (re == HPChallengeRewardTypes.ECO) {
                sb2.append(hpc.getString("inventory.icon.reward.currency", p).replace("{amount}", value));
            } else if (re == HPChallengeRewardTypes.GIVE_ITEM) {
                sb2.append(hpc.getString("inventory.icon.reward.item-give", p)
                        .replace("{amount}", String.valueOf(getRewardItemAmount()))
                        .replace("{item}", WordUtils.capitalize(value.toLowerCase().replaceAll("_", " "))));
            } else if (re == HPChallengeRewardTypes.ADD_GROUP) {
                sb2.append(hpc.getString("inventory.icon.reward.group-add", p).replace("{group}", value));
            } else if (re == HPChallengeRewardTypes.REMOVE_GROUP) {
                sb2.append(hpc.getString("inventory.icon.reward.group-remove", p).replace("{group}", value));
            }
        }

        player.addXp(getGainedXP());
        reward.reward(p);
        if (MainConfig.get().getChallenges().BROADCAST_CHALLENGE_COMPLETE) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                hpc.sendMessage("commands.challenges.challenge-complete", pl, "{challenge}", getMainName(), "{player}", p.getName(), "{name}", p.getName());
            }
        }
        String message = HeadsPlusMessagesManager.get().getString("commands.challenges.reward-string", p);
        String[] msgs = message.split("\\\\n");
        for (String str : msgs) {
            hpc.sendMessage(str.replace("{reward}", sb2.toString()).replaceAll("\\{xp}", String.valueOf(getGainedXP())), p, false);
        }
    }
}
