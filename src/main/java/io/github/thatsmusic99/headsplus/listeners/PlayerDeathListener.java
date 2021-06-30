package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Random;

public class PlayerDeathListener extends HeadsPlusListener<PlayerDeathEvent> {

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(PlayerDeathEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerDeathEvent.class, "PlayerDeathEvent", this), HeadsPlus.get());
        addPossibleData("player", "<Player>");
        addPossibleData("enabled", "true", "false");
        addPossibleData("is-mythic-mob", "true", "false");
        addPossibleData("not-wg-restricted", "true", "false");
        addPossibleData("amount", "1", "2", "3", "4");
        addPossibleData("killer", "<Player>");
        addPossibleData("fixed-chance", "<Chance>");
        addPossibleData("random-chance", "<Chance>");
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().PLAYER_DROPS;
    }

    @Override
    public void onEvent(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        addData("player", victim.getName());
        // Make sure the entity isn't from MythicMobs
        if (addData("is-mythic-mob", HPUtils.isMythicMob(event.getEntity()))) return;
        if (!addData("not-wg-restricted", Bukkit.getPluginManager().getPlugin("WorldGuard") == null
                || FlagHandler.canDrop(event.getEntity().getLocation(), event.getEntity().getType()))) return;
        if (!HPUtils.runBlacklistTests(event.getEntity())) return;
        double fixedChance = addData("fixed-chance", ConfigMobs.get().getPlayerChance(victim.getName()));
        if (fixedChance == 0) return;
        double randomChance = addData("random-chance", new Random().nextDouble() * 100);
        if (killer != null) {
            fixedChance = HPUtils.calculateChance(fixedChance, randomChance, event.getEntity().getKiller());
            addData("killer", event.getEntity().getKiller().getName());
        }
        if (randomChance > fixedChance) return;
        int amount = addData("amount", HPUtils.getAmount(fixedChance));
        double lostprice = 0.0;
        double price = ConfigMobs.get().getPlayerPrice(victim.getName());
        Economy economy = HeadsPlus.get().getEconomy();
        if (MainConfig.get().getPlayerDrops().ADJUST_PRICE_ACCORDING_TO_PRICE) {
            double playerPrice;
            if (!MainConfig.get().getPlayerDrops().USE_VICTIM_BALANCE
                    && killer != null
                    && economy.getBalance(killer) > 0.0) {
                playerPrice = economy.getBalance(killer);
            } else {
                playerPrice = economy.getBalance(victim);
            }
            price = playerPrice * (MainConfig.get().getPlayerDrops().PERCENTAGE_OF_BALANCE_AS_PRICE / 100);
            lostprice = playerPrice * (MainConfig.get().getPlayerDrops().PERCENTAGE_TAKEN_OFF_VICTIM / 100);
        }

        // TODO - lore
        HeadManager.HeadInfo headInfo = new HeadManager.HeadInfo().withTexture(victim.getName())
                .withDisplayName(ConfigMobs.get().getPlayerDisplayName(victim.getName()));

        Location location = victim.getLocation();
        PlayerHeadDropEvent phdEvent = new PlayerHeadDropEvent(victim, killer, headInfo, location, amount);
        Bukkit.getPluginManager().callEvent(phdEvent);

        if (phdEvent.isCancelled()) return;
        if (lostprice > 0.0) {
            economy.withdrawPlayer(victim, lostprice);
            HeadsPlusMessagesManager.get().sendMessage("event.lost-money", victim, "{player}", killer.getName(), "{price}", MainConfig.get().fixBalanceStr(price));
        }
        double finalPrice = price;
        headInfo.buildHead().thenAccept(item -> {
            item.setAmount(amount);
            PersistenceManager.get().setSellType(item, "mobs_player");
            PersistenceManager.get().setSellPrice(item, finalPrice);
            location.getWorld().dropItem(location, item);
        });
    }
}
