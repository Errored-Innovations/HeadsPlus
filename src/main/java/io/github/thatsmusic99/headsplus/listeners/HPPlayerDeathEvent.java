package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.Head;
import io.github.thatsmusic99.headsplus.api.events.PlayerHeadDropEvent;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Random;

public class HPPlayerDeathEvent extends HeadsPlusListener<PlayerDeathEvent> {

    private final HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();

    public HPPlayerDeathEvent() {
        super();
        Bukkit.getPluginManager().registerEvent(PlayerDeathEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(PlayerDeathEvent.class, "PlayerDeathEvent", this), HeadsPlus.getInstance());
    }

    @Override
    public void onEvent(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        addData("player", victim.getName());
        // Make sure head drops are enabled
        if (!addData("enabled", hp.isDropsEnabled())) return;
        // Make sure the entity isn't from MythicMobs
        if (addData("is-mythic-mob", HPUtils.isMythicMob(event.getEntity()))) return;
        if (!addData("not-wg-restricted", Bukkit.getPluginManager().getPlugin("WorldGuard") == null || FlagHandler.canDrop(event.getEntity().getLocation(), event.getEntity().getType()))) return;
        if (!HPUtils.runBlacklistTests(event.getEntity())) return;
        double fixedChance = addData("fixed-chance", hpch.getChance("player"));
        if (fixedChance == 0) return;
        double randomChance = addData("random-chance", new Random().nextDouble() * 100);
        if (event.getEntity().getKiller() != null) {
            fixedChance = HPUtils.calculateChance(fixedChance, randomChance, event.getEntity().getKiller());
        }
        if (randomChance <= fixedChance) {
            int amount = addData("amount", HPUtils.getAmount(fixedChance));
            double lostprice = 0.0;
            double price = hpch.getPrice("player");
            Economy economy = HeadsPlus.getInstance().getEconomy();
            if (hp.getConfiguration().getPerks().pvp_player_balance_competition
                    && killer != null
                    && economy.getBalance(killer) > 0.0) {
                double playerprice = economy.getBalance(killer);
                price = playerprice * hp.getConfiguration().getPerks().pvp_balance_for_head;
                lostprice = economy.getBalance(killer) * hp.getConfiguration().getPerks().pvp_percentage_lost;
            }
            Head head = new EntityHead("PLAYER").withAmount(amount)
                    .withDisplayName(ChatColor.RESET + hpch.getDisplayName("player").replace("{player}", victim.getName()))
                    .withPrice(price)
                    .withLore(hpch.getLore(victim.getName(), price))
                    .withPlayerName(victim.getName());
            Location location = victim.getLocation();
            PlayerHeadDropEvent phdEvent = new PlayerHeadDropEvent(victim, killer, head, location, amount);
            Bukkit.getPluginManager().callEvent(phdEvent);
            if (!phdEvent.isCancelled()) {
                location.getWorld().dropItem(location, head.getItemStack());
                if (lostprice > 0.0) {
                    economy.withdrawPlayer(victim, lostprice);
                    hp.getMessagesConfig().sendMessage("event.lost-money", victim, "{player}", killer.getName(), "{price}", hp.getConfiguration().fixBalanceStr(price));
                }
            }
        }
    }
}
