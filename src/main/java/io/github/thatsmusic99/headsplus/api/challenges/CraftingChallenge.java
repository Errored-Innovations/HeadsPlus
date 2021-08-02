package io.github.thatsmusic99.headsplus.api.challenges;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.sql.StatisticsSQLManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CraftingChallenge extends Challenge {

    public CraftingChallenge(String key, ConfigSection section, ItemStack icon, ItemStack completeIcon) {
        super(key, section, icon, completeIcon);
    }

    @Override
    public CompletableFuture<Boolean> canComplete(Player p) {
        return StatisticsSQLManager.get().getStat(p.getUniqueId(), StatisticsSQLManager.CollectionType.CRAFTING, getHeadType())
                .thenApply(total -> total >= getRequiredHeadAmount());
    }

    @Override
    public String getCacheID() {
        if (getHeadType().equals("total")) return "CRAFTING";
        return "CRAFTING_" + getHeadType();
    }

    @Override
    public CompletableFuture<Integer> getStatFuture(UUID uuid) {
        if (getHeadType().equals("total")) return StatisticsSQLManager.get().getStat(uuid, StatisticsSQLManager.CollectionType.CRAFTING);
        return StatisticsSQLManager.get().getStat(uuid, StatisticsSQLManager.CollectionType.CRAFTING, getHeadType());
    }

}
