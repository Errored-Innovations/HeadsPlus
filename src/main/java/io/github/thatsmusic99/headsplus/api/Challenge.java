package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.challenges.CraftingChallenge;
import io.github.thatsmusic99.headsplus.api.challenges.MiscChallenge;
import io.github.thatsmusic99.headsplus.api.challenges.MobKillChallenge;
import io.github.thatsmusic99.headsplus.config.MessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.RewardsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class Challenge {

    private final String configName;
    private final String mainName;
    private final String header;
    private final List<String> description;
    private final int requiredHeadAmount;
    private final String reward;
    private final int difficulty;
    private final ItemStack icon;
    private final ItemStack completeIcon;
    private final String headType;

    public Challenge(String key, ConfigSection section, ItemStack icon, ItemStack completeIcon) {
        this.configName = key;
        this.mainName = Objects.requireNonNull(section.getString("name"), "Challenge name for " + key + " not found!");
        this.header = Objects.requireNonNull(section.getString("header"), "Challenge header for " + key + " not found!");
        this.description = section.getStringList("description");
        if (!section.contains("min")) throw new NullPointerException("Minimum head count (min) for " + key + " not found!");
        this.requiredHeadAmount = section.getInteger("min");
        this.headType = Objects.requireNonNull(section.getString("head-type"), "Challenge head type for " + key + " not found!");
        String reward = section.getString("reward");
        if (reward == null) throw new NullPointerException("No reward found for challenge " + key + "!");
        if (!(reward.startsWith("levels_") || reward.startsWith("challenges_"))) {
            reward = "challenges_" + reward;
        }
        if (!RewardsManager.get().contains(reward))
            throw new NullPointerException("Reward ID " + reward + " was not found for challenge " + key + "!");
        this.reward = reward;
        this.difficulty = section.getInteger("difficulty", 1);
        this.icon = icon;
        this.completeIcon = completeIcon;
    }

    public static Challenge fromConfigSection(String id, ConfigSection section, ItemStack icon, ItemStack completeIcon) {
        String type = section.getString("type");
        if (type == null) throw new NullPointerException("No type found for challenge " + id + "!");
        switch (type.toUpperCase()) {
            case "LEADERBOARD":
            case "MOBKILL":
                return new MobKillChallenge(id, section, icon, completeIcon);
            case "CRAFTING":
                return new CraftingChallenge(id, section, icon, completeIcon);
            case "MISC":
                return new MiscChallenge(id, section, icon, completeIcon);
            default:
                throw new IllegalArgumentException("No such challenge type " + type + " for " + id + "!");
        }
    }

    public String getConfigName() {
        return configName;
    }

    public int getRequiredHeadAmount() {
        return requiredHeadAmount;
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
        return RewardsManager.get().getReward(reward);
    }

    public String getHeadType() {
        return headType;
    }

    public int getGainedXP() {
        return getReward().getXp();
    }

    public abstract CompletableFuture<Boolean> canComplete(Player p);

    public abstract String getCacheID();

    public abstract CompletableFuture<Integer> getStatFuture(UUID uuid);

    public abstract int getStatSync(UUID uuid);

    public boolean isComplete(Player p) {
        return HPPlayer.getHPPlayer(p.getUniqueId()).getCompleteChallenges().contains(getConfigName());
    }

    public void complete(Player p) {
        MessagesManager hpc = MessagesManager.get();
        HPPlayer player = HPPlayer.getHPPlayer(p.getUniqueId());
        player.addCompleteChallenge(this);

        player.addXp(getGainedXP());
        getReward().rewardPlayer(this, p);
        if (MainConfig.get().getChallenges().BROADCAST_CHALLENGE_COMPLETE) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                hpc.sendMessage("commands.challenges.challenge-complete", pl, "{challenge}", getMainName(), "{player}", p.getName(), "{name}", p.getName());
            }
        }
        String message = MessagesManager.get().getString("commands.challenges.reward-string", p);
        String[] msgs = message.split("\\\\n");
        for (String str : msgs) {
            hpc.sendMessage(str.replace("{reward}", getReward().getRewardString(p)).replaceAll("\\{xp}", String.valueOf(getGainedXP())), p, false);
        }
    }
}
