package io.github.thatsmusic99.headsplus.api.challenges;

import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import io.github.thatsmusic99.headsplus.api.Challenge;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MiscChallenge extends Challenge {

    public MiscChallenge(String id, ConfigSection section, ItemStack icon, ItemStack completeIcon) {
        super(id, section, icon, completeIcon);
    }

    @Override
    public CompletableFuture<Boolean> canComplete(Player p) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public String getCacheID() {
        return "MISC_sike";
    }

    @Override
    public CompletableFuture<Integer> getStatFuture(UUID uuid) {
        return CompletableFuture.completedFuture(0);
    }

}
