package io.github.thatsmusic99.headsplus.api.challenges;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.Challenge;
import io.github.thatsmusic99.headsplus.sql.StatisticsSQLManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MobKillChallenge extends Challenge {

    public MobKillChallenge(String key, ConfigSection section, ItemStack icon, ItemStack completeIcon) {
        super(key, section, icon, completeIcon);
    }

    @Override
    public CompletableFuture<Boolean> canComplete(Player p) {
        return StatisticsSQLManager.get().getStatMeta(p.getUniqueId(), StatisticsSQLManager.CollectionType.HUNTING, "entity=" + getHeadType())
                .thenApply(total -> total >= getRequiredHeadAmount());
    }

    @Override
    public String getCacheID() {
        if (getHeadType().equals("total")) return "HUNTING";
        return "HUNTING_entity=" + getHeadType();
    }

    @Override
    public CompletableFuture<Integer> getStatFuture(UUID uuid) {
        if (getHeadType().equals("total")) return StatisticsSQLManager.get().getStat(uuid, StatisticsSQLManager.CollectionType.HUNTING);
        return StatisticsSQLManager.get().getStat(uuid, StatisticsSQLManager.CollectionType.HUNTING, getHeadType());
    }

}
