package io.github.thatsmusic99.headsplus.api;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reward implements Cloneable {

    private final HPChallengeRewardTypes type;
    private double money;
    private int xp;
    private ItemStack item;
    private String group;
    private boolean consoleSender = false;
    private final boolean multiply;
    private final String name;
    private List<String> commands;
    private String rewardString = null;

    public Reward(String name, HPChallengeRewardTypes type, Object value, int amount, String sender, int xp, boolean multiply, String... rewardString) {
        this.name = name;
        switch (type) {
            case ECO:
                money = Double.parseDouble(String.valueOf(value));
                break;
            case ADD_GROUP:
            case REMOVE_GROUP:
                group = String.valueOf(value);
                break;
            case RUN_COMMAND:
                if (value instanceof List) {
                    commands = new ArrayList<>((List<String>) value);
                } else {
                    commands = Collections.singletonList(String.valueOf(value));
                }
                break;
            case GIVE_ITEM:
                item = new ItemStack(Material.getMaterial(String.valueOf(value)), amount);
                break;
        }
        this.type = type;
        this.multiply = multiply;
        if (sender != null && sender.equalsIgnoreCase("console")) {
            consoleSender = true;
        }
        this.xp = xp;
        if (rewardString.length > 0) {
            this.rewardString = rewardString[0];
        }
    }

    public String getRewardString() {
        return rewardString;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public boolean isMultiply() {
        return multiply;
    }

    public boolean isConsoleSender() {
        return consoleSender;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public HPChallengeRewardTypes getType() {
        return type;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getGroup() {
        return group;
    }

    public void reward(Player player) {
        HeadsPlus hp = HeadsPlus.get();
        HeadsPlusMessagesManager hpc = HeadsPlusMessagesManager.get();
        if (hp.isVaultEnabled()) {
            if (type == HPChallengeRewardTypes.ECO) {
                    hp.getEconomy().depositPlayer(player, getMoney());
            } else if (type == HPChallengeRewardTypes.ADD_GROUP) {
                if (!hp.getPermissions().playerInGroup(player, getGroup())) {
                    hp.getPermissions().playerAddGroup(player, getGroup());
                }
            } else if (type == HPChallengeRewardTypes.REMOVE_GROUP) {
                if (hp.getPermissions().playerInGroup(player, getGroup())) {
                    hp.getPermissions().playerRemoveGroup(player, getGroup());
                }
            }
        } else {
            hp.getLogger().warning(hpc.getString("startup.no-vault-2"));
        }
        if (type == HPChallengeRewardTypes.GIVE_ITEM) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(getItem());
            }
        } else if (type == HPChallengeRewardTypes.RUN_COMMAND) {
            if (isConsoleSender()) {
                for (String str : getCommands()) {
                    Bukkit.dispatchCommand(hp.getServer().getConsoleSender(), String.valueOf(str).replaceAll("\\{player}", player.getName()));
                }
            } else {
                for (String str : getCommands()) {
                    player.performCommand(String.valueOf(str).replaceAll("\\{player}", player.getName()));
                }
            }
        }
    }

    public Reward clone() {
        try {
            return (Reward) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
