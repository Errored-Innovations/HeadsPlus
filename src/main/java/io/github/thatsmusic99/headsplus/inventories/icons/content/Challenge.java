package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.config.ConfigInventories;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.placeholders.CacheManager;
import io.github.thatsmusic99.headsplus.util.HPUtils;
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
                    challenge.canComplete(player).thenAcceptAsync(result -> {
                        if (result) {
                            challenge.complete(player);
                            item.setType(challenge.getCompleteIcon().getType());
                            initNameAndLore("challenge", player);
                            event.getInventory().setItem(event.getSlot(), item);
                        } else {
                            HeadsPlusMessagesManager.get().sendMessage("commands.challenges.cant-complete-challenge", player);
                        }
                    }, HeadsPlus.sync);
                } else {
                    HeadsPlusMessagesManager.get().sendMessage("commands.challenges.already-complete-challenge", player);
                }
            } else {
                HPPlayer hpPlayer = HPPlayer.getHPPlayer(player);
                if (hpPlayer.hasChallengePinned(challenge)) {
                    hpPlayer.removeChallengePin(challenge).thenAcceptAsync(why -> {
                        initNameAndLore("challenge", player);
                        event.getInventory().setItem(event.getSlot(), item);
                    }, HeadsPlus.sync);
                } else {
                    hpPlayer.addChallengePin(challenge).thenAcceptAsync(why -> {
                        initNameAndLore("challenge", player);
                        event.getInventory().setItem(event.getSlot(), item);
                    }, HeadsPlus.sync);
                }
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
        meta.setDisplayName(HeadsPlusMessagesManager.get().formatMsg(ConfigInventories.get().getString("icons.challenge.display-name")
                .replaceAll("\\{challenge-name}", challenge.getChallengeHeader()), player));
        List<String> lore = new ArrayList<>();
        for (String loreStr : ConfigInventories.get().getStringList("icons.challenge.lore")) {
            if (loreStr.contains("{challenge-lore}")) {
                for (String loreStr2 : challenge.getDescription()) {
                    lore.add(HeadsPlusMessagesManager.get().formatMsg(loreStr2, player));
                }
            } else {
                HPUtils.parseLorePlaceholders(lore, loreStr,
                        new HPUtils.PlaceholderInfo("{completed}",
                                HeadsPlusMessagesManager.get().getString("commands.challenges.challenge-completed", player),
                                challenge.isComplete(player)),
                        new HPUtils.PlaceholderInfo("{pinned}",
                                HeadsPlusMessagesManager.get().getString("inventory.icon.challenge.pinned", player),
                                HPPlayer.getHPPlayer(player).hasChallengePinned(challenge)),
                        new HPUtils.PlaceholderInfo("{reward}", reward, true),
                        new HPUtils.PlaceholderInfo("{challenge-reward}", reward, true),
                        new HPUtils.PlaceholderInfo("{xp}", challenge.getGainedXP(), true),
                        new HPUtils.PlaceholderInfo("{challenge-xp}", challenge.getGainedXP(), true),
                        new HPUtils.PlaceholderInfo("{total}", challenge.getRequiredHeadAmount(), true),
                        new HPUtils.PlaceholderInfo("{heads}",
                                // TODO - use supplier, not this
                                CacheManager.get().getStat(challenge.getCacheID(), challenge.getStatFuture(player.getUniqueId())), true));
            }

        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
