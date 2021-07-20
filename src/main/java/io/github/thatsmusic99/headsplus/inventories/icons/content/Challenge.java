package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.HeadsPlusAPI;
import io.github.thatsmusic99.headsplus.config.ConfigInventories;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Challenge extends Content {

    private io.github.thatsmusic99.headsplus.api.Challenge challenge;
    private String reward;

    public Challenge(io.github.thatsmusic99.headsplus.api.Challenge challenge, Player player) {
        super(challenge.isComplete(player) ? challenge.getCompleteIcon().clone() : challenge.getIcon().clone());
        this.challenge = challenge;
        this.reward = challenge.getReward().getRewardString(player);
        initNameAndLore("challenge", player);
    }

    public Challenge() {
        super();
    }

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        try {
            if (event.isLeftClick()) {
                if (!challenge.isComplete(player)) {
                    if (challenge.canComplete(player)) {
                        challenge.complete(player);
                        item.setType(challenge.getCompleteIcon().getType());
                        item.setDurability(challenge.getCompleteIcon().getDurability());
                        initNameAndLore("challenge", player);
                        event.getInventory().setItem(event.getSlot(), item);
                    } else {
                        hpc.sendMessage("commands.challenges.cant-complete-challenge", player);
                    }
                } else {
                    hpc.sendMessage("commands.challenges.already-complete-challenge", player);
                }
            } else {
                HPPlayer hpPlayer = HPPlayer.getHPPlayer(player);
                if (hpPlayer.hasChallengePinned(challenge)) {
                    hpPlayer.removeChallengePin(challenge);
                } else {
                    hpPlayer.addChallengePin(challenge);
                }
                initNameAndLore("challenge", player);
                event.getInventory().setItem(event.getSlot(), item);
            }
        }catch (NullPointerException ignored) {
        }
        return false;
    }

    @Override
    public String getId() {
        return "challenge";
    }

    @Override
    public void initNameAndLore(String id, Player player) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(hpc.formatMsg(ConfigInventories.get().getString("icons.challenge.display-name")
                .replaceAll("\\{challenge-name}", challenge.getChallengeHeader()), player));
        List<String> lore = new ArrayList<>();
        for (String loreStr : ConfigInventories.get().getStringList("icons.challenge.lore")) {
            if (loreStr.contains("{challenge-lore}")) {
                for (String loreStr2 : challenge.getDescription()) {
                    lore.add(hpc.formatMsg(loreStr2, player));
                }
            } else {
                if (loreStr.contains("{completed}")) {
                    if (challenge.isComplete(player)) {
                        lore.add(hpc.getString("commands.challenges.challenge-completed", player));
                    }
                } else if (loreStr.contains("{pinned}")) {
                    if (HPPlayer.getHPPlayer(player).hasChallengePinned(challenge)) {
                        lore.add(hpc.getString("inventory.icon.challenge.pinned", player));
                    }
                } else {
                    String str = hpc.formatMsg(hpc.completed(loreStr, player, challenge), player);
                    try {
                        str = str.replace("{reward}", reward)
                                .replace("{challenge-reward}", reward);
                    } catch (NullPointerException ignored) {

                    }
                    str = str.replaceAll("(\\{xp}|\\{challenge-xp})", String.valueOf(challenge.getGainedXP()))
                            .replaceAll("\\{heads}", String.valueOf(HeadsPlusAPI.getPlayerInLeaderboards(player,
                                    challenge.getHeadType(),
                                    challenge.getDatabaseType())))
                            .replaceAll("\\{total}", String.valueOf(challenge.getRequiredHeadAmount()));
                    lore.add(str);
                }

            }

        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
