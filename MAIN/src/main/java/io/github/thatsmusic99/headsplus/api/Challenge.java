package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import io.github.thatsmusic99.headsplus.config.challenges.HeadsPlusChallengeTypes;
import io.github.thatsmusic99.headsplus.locale.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Challenge {

    // I
    private String configName;
    private String mainName;
    private String header;
    private List<String> description;
    private int requiredHeadAmount;
    private HeadsPlusChallengeTypes challengeType;
    private Reward reward;
    private int difficulty;
    private ItemStack icon;
    private ItemStack completeIcon;
    private String headType;

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
        HeadsPlusAPI hapi = HeadsPlus.getInstance().getAPI();
        if (getChallengeType() == HeadsPlusChallengeTypes.MISC) {
            return true;
        } else if (getChallengeType() == HeadsPlusChallengeTypes.CRAFTING) {
            return hapi.getPlayerInLeaderboards(p, getHeadType().equals("total") ? "total" : HeadsPlus.getInstance().getAPI().strToEntityType(getHeadType()).name(), "headspluscraft") >= getRequiredHeadAmount();
        } else if (getChallengeType() == HeadsPlusChallengeTypes.LEADERBOARD) {
            return hapi.getPlayerInLeaderboards(p, getHeadType().equals("total") ? "total" : HeadsPlus.getInstance().getAPI().strToEntityType(getHeadType()).name(), "headspluslb") >= getRequiredHeadAmount();
        } else {
            return hapi.getPlayerInLeaderboards(p, getHeadType().equals("total") ? "total" : HeadsPlus.getInstance().getAPI().strToEntityType(getHeadType()).name(), "headsplussh") >= getRequiredHeadAmount();
        }
    }

    public boolean isComplete(Player p) {
        return HeadsPlus.getInstance().getScores().getCompletedChallenges(p.getUniqueId().toString()).contains(getConfigName());
    }

    public void complete(Player p) {
        complete(p, null, 0);
    }

    public void complete(Player p, Inventory i, int slot) {
        HeadsPlus hp = HeadsPlus.getInstance();
        HPPlayer player = HPPlayer.getHPPlayer(p);
        player.addCompleteChallenge(this);


        List<String> lore = new ArrayList<>();
        for (String st : getDescription()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', st));
        }
        StringBuilder sb2 = new StringBuilder();
        HPChallengeRewardTypes re = reward.getType();
        if (re != HPChallengeRewardTypes.RUN_COMMAND) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.GOLD).append("Reward: ");
            String rewardString = reward.getRewardString();
            if (rewardString != null) {
                sb.append(ChatColor.GREEN).append(rewardString);
                sb2.append(rewardString);
            } else if (re == HPChallengeRewardTypes.ECO) {
                sb.append(ChatColor.GREEN).append("$").append(getRewardValue());
                sb2.append("$").append(getRewardValue());
            } else if (re == HPChallengeRewardTypes.GIVE_ITEM) {
                try {
                    Material.valueOf(getRewardValue().toString().toUpperCase());
                    sb
                            .append(ChatColor.GREEN)
                            .append(getRewardItemAmount())
                            .append(" ")
                            .append(HeadsPlus.capitalize(getRewardValue().toString().replaceAll("_", " ")));
                    sb2
                            .append(getRewardItemAmount())
                            .append(" ")
                            .append(getRewardValue().toString().replaceAll("_", " "));
                } catch (IllegalArgumentException e) {
                    //
                }
            }
            lore.add(sb.toString());
        }

        if (i != null) {
            ItemStack is = getCompleteIcon();
            ItemMeta im = is.getItemMeta();
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', getChallengeHeader()));
            lore.add(ChatColor.GOLD + "XP: " + ChatColor.GREEN + getGainedXP());
            lore.add(ChatColor.GREEN + "Completed!");
            im.setLore(lore);
            is.setItemMeta(im);
            is = hp.getNBTManager().setIcon(is, new io.github.thatsmusic99.headsplus.config.customheads.icons.Challenge());
            i.setItem(slot, is);
        }
        player.addXp(getGainedXP());
        reward.reward(p);
        if (hp.getConfiguration().getMechanics().getBoolean("broadcasts.challenge-complete")) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.sendMessage(hp.getMessagesConfig().getString("challenge-complete")
                        .replaceAll("\\{challenge}", getMainName())
                        .replaceAll("\\{name}", p.getName()));
            }
        }

        p.sendMessage(hp.getThemeColour(4) + LocaleManager.getLocale().getReward() + hp.getThemeColour(2) + sb2.toString());
        p.sendMessage(hp.getThemeColour(4) + "XP: " + hp.getThemeColour(2) + getGainedXP());
    }
}
