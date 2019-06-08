package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesConfig;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class CLevel implements RLevel {

    // T
    private String configName;
    private String displayName;
    private int requiredXP;
    private double addedVersion;
    private boolean rEnabled;
    private HPChallengeRewardTypes rewardType;
    private Object rewardValue;
    private int rewardItemAmount;
    private String sender;

    public CLevel(String configName, String displayName, int requiredXP, double addedVersion, boolean e, HPChallengeRewardTypes rewardType, Object rewardValue, int rewardItemAmount, String sender) {
        this.configName = configName;
        this.displayName = displayName;
        this.requiredXP = requiredXP;
        this.addedVersion = addedVersion;
        rEnabled = e;
        this.rewardType = rewardType;
        this.rewardValue = rewardValue;
        this.rewardItemAmount = rewardItemAmount;
        this.sender = sender;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int getRequiredXP() {
        return requiredXP;
    }

    @Override
    public double getAddedVersion() {
        return addedVersion;
    }

    public HPChallengeRewardTypes getRewardType() {
        return rewardType;
    }

    public boolean isrEnabled() {
        return rEnabled;
    }

    public int getRewardItemAmount() {
        return rewardItemAmount;
    }

    public Object getRewardValue() {
        return rewardValue;
    }

    public String getSender() {
        return sender;
    }

    public void reward(Player p) {
        HPChallengeRewardTypes re = getRewardType();
        HeadsPlus hp = HeadsPlus.getInstance();
        HeadsPlusMessagesConfig hpc = hp.getMessagesConfig();
        Permission perms = hp.getPermissions();

        if (re == HPChallengeRewardTypes.ECO) {
            if (hp.econ()) {
                hp.getEconomy().depositPlayer(p, Double.valueOf(String.valueOf(getRewardValue())));
            } else {
                hp.getLogger().warning(hpc.getString("no-vault-2"));
            }

        } else if (re == HPChallengeRewardTypes.ADD_GROUP) {
            if (hp.econ()) {
                if (!perms.playerInGroup(p, (String) getRewardValue())) {
                    perms.playerAddGroup(p, (String) getRewardValue());
                }
            } else {
                hp.getLogger().warning(hpc.getString("no-vault-2"));
            }

        } else if (re == HPChallengeRewardTypes.REMOVE_GROUP) {
            if (hp.econ()) {
                if (perms.playerInGroup(p, (String) getRewardValue())) {
                    perms.playerRemoveGroup(p, (String) getRewardValue());
                }
            } else {
                hp.getLogger().warning(hpc.getString("no-vault-2"));
            }
        } else if (re == HPChallengeRewardTypes.GIVE_ITEM) {
            try {
                ItemStack is = new ItemStack(Material.valueOf(((String) getRewardValue()).toUpperCase()), getRewardItemAmount());
                if (p.getInventory().firstEmpty() != -1) {
                    p.getInventory().addItem(is);
                }
            } catch (IllegalArgumentException ex) {
                Logger log = hp.getLogger();
                log.severe("Couldn't give reward to " + p.getName() + "! Details:");
                log.warning("Level name: " + getConfigName());
                log.warning("Item name: " + getRewardValue());
                log.warning("Item amount: " + getRewardItemAmount());
            }
        } else if (re == HPChallengeRewardTypes.RUN_COMMAND) {
            if (sender == null
                    || sender.isEmpty()
                    || sender.equalsIgnoreCase("player")) {
                p.performCommand(String.valueOf(getRewardValue()).replaceAll("\\{player}", p.getName()));
            } else if (sender.equalsIgnoreCase("console")) {
                Bukkit.dispatchCommand(hp.getServer().getConsoleSender(), String.valueOf(getRewardValue()).replaceAll("\\{player}", p.getName()));
            }
        }
    }

}
