package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class Level extends BaseLevel {

    // T
    private final String configName;
    private final String displayName;
    private final int requiredXP;
    private final double addedVersion;
    private final boolean rEnabled;
    private final HPChallengeRewardTypes rewardType;
    private final Object rewardValue;
    private final int rewardItemAmount;
    private final String sender;

    public Level(String configName, String displayName, int requiredXP, double addedVersion, boolean e, HPChallengeRewardTypes rewardType, Object rewardValue, int rewardItemAmount, String sender) {
        super(configName, displayName, requiredXP, addedVersion);
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
        HeadsPlusMessagesManager hpc = hp.getMessagesConfig();
        Permission perms = hp.getPermissions();

        if (re == HPChallengeRewardTypes.ECO) {
            if (hp.econ()) {
                hp.getEconomy().depositPlayer(p, Double.parseDouble(String.valueOf(getRewardValue())));
            } else {
                hp.getLogger().warning(hpc.getString("startup.no-vault-2"));
            }

        } else if (re == HPChallengeRewardTypes.ADD_GROUP) {
            if (hp.econ()) {
                if (!perms.playerInGroup(p, (String) getRewardValue())) {
                    perms.playerAddGroup(p, (String) getRewardValue());
                }
            } else {
                hp.getLogger().warning(hpc.getString("startup.no-vault-2"));
            }

        } else if (re == HPChallengeRewardTypes.REMOVE_GROUP) {
            if (hp.econ()) {
                if (perms.playerInGroup(p, (String) getRewardValue())) {
                    perms.playerRemoveGroup(p, (String) getRewardValue());
                }
            } else {
                hp.getLogger().warning(hpc.getString("startup.no-vault-2"));
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
                log.warning("BaseLevel name: " + getConfigName());
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
