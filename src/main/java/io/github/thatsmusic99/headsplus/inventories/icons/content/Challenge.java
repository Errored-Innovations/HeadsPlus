package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HeadsPlusAPI;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigItems;
import io.github.thatsmusic99.headsplus.config.challenges.HPChallengeRewardTypes;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Challenge extends Content {

    private io.github.thatsmusic99.headsplus.api.Challenge challenge;
    private String reward;

    public Challenge(io.github.thatsmusic99.headsplus.api.Challenge challenge, Player player) {
        super(challenge.isComplete(player) ? challenge.getCompleteIcon().clone() : challenge.getIcon().clone());
        this.challenge = challenge;
        initReward(player);
        initNameAndLore("challenge", player);
    }

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        try {
            if (challenge != null) {
                if (!challenge.isComplete(player)) {
                    if (challenge.canComplete(player)) {
                        challenge.complete(player);
                        item.setType(challenge.getCompleteIcon().getType());
                        item.setDurability(challenge.getCompleteIcon().getDurability());
                    } else {
                        player.sendMessage(hpc.getString("commands.challenges.cant-complete-challenge", player));
                    }
                } else {
                    player.sendMessage(hpc.getString("commands.challenges.already-complete-challenge", player));
                }
            }
            event.setCancelled(true);
        }catch (NullPointerException ignored) {
        } catch (SQLException ex) {
            DebugPrint.createReport(ex, "Completing challenge", false, player);
        }
        return false;
    }

    @Override
    public String getId() {
        return "challenge";
    }

    @Override
    public void initNameAndLore(String id, Player player) {
        HeadsPlusConfigItems items = hp.getItems();
        HeadsPlusAPI api = HeadsPlus.getInstance().getAPI();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(hpc.formatMsg(items.getConfig().getString("icons.challenge.display-name")
                .replaceAll("\\{challenge-name}", challenge.getChallengeHeader()), player));
        List<String> lore = new ArrayList<>();
        for (String loreStr : items.getConfig().getStringList("icons.challenge.lore")) {
            try {
                if (loreStr.contains("{challenge-lore}")) {
                    for (String loreStr2 : challenge.getDescription()) {
                        lore.add(hpc.formatMsg(loreStr2, player));
                    }
                } else {
                    if (loreStr.contains("{completed}")) {
                        if (challenge.isComplete(player)) {
                            lore.add(hpc.getString("commands.challenges.challenge-completed", player));
                        }
                    } else {
                        lore.add(hpc.formatMsg(hpc.completed(loreStr, player, challenge), player)
                                .replace("{reward}", reward)
                                .replaceAll("\\{xp}", String.valueOf(challenge.getGainedXP()))
                                .replaceAll("\\{heads}", String.valueOf(api.getPlayerInLeaderboards(player,
                                        challenge.getHeadType(),
                                        challenge.getChallengeType().getDatabase(),
                                        true)))
                                .replaceAll("\\{total}", String.valueOf(challenge.getRequiredHeadAmount())));
                    }

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private void initReward(Player player) {
        reward = challenge.getReward().getRewardString();
        String value = challenge.getRewardValue().toString();
        if (reward == null) {
            HPChallengeRewardTypes type = challenge.getRewardType();
            if (type == HPChallengeRewardTypes.ECO) {
                reward = hpc.getString("inventory.icon.reward.currency", player).replace("{amount}", value);
            } else if (type == HPChallengeRewardTypes.GIVE_ITEM) {
                reward = hpc.getString("inventory.icon.reward.item-give", player)
                        .replace("{amount}", String.valueOf(challenge.getRewardItemAmount()))
                        .replace("{item}", WordUtils.capitalize(value.toLowerCase().replaceAll("_", " ")));
            } else if (type == HPChallengeRewardTypes.ADD_GROUP) {
                reward = hpc.getString("inventory.icon.reward.group-add", player).replace("{group}", value);
            } else if (type == HPChallengeRewardTypes.REMOVE_GROUP) {
                reward = hpc.getString("inventory.icon.reward.group-remove", player).replace("{group}", value);
            }
        }
    }
}
