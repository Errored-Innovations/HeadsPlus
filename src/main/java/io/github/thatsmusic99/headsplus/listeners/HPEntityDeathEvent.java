package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.util.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class HPEntityDeathEvent extends HeadsPlusListener<EntityDeathEvent> {

    private final HeadsPlusConfigHeads hpch = HeadsPlus.getInstance().getHeadsConfig();

    public HPEntityDeathEvent() {
        super();
        Bukkit.getPluginManager().registerEvent(EntityDeathEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(EntityDeathEvent.class, "EntityDeathEvent"), HeadsPlus.getInstance());
    }

    @Override
    public void onEvent(EntityDeathEvent event) {
        // Make sure head drops are enabled
        if (!addData("enabled", hp.isDropsEnabled())) return;
        // Make sure the entity is valid
        if (!EntityDataManager.ableEntities.contains(addData("entity-type", event.getEntityType().name()))) return;
        // Make sure the entity isn't from MythicMobs
        if (addData("is-mythic-mob", isMythicMob(event.getEntity()))) return;
        // And make sure there is no WG region saying no
        // I SWEAR TO GOD WORLDGUARD IS SUCH A BRAT
        if (!addData("not-wg-restricted", Bukkit.getPluginManager().getPlugin("WorldGuard") == null || FlagHandler.canDrop(event.getEntity().getLocation(), event.getEntity().getType()))) return;
        // TODO New blacklist checks go here
        if (!runBlacklistTests(event.getEntity())) return;
        //
        if (addData("spawn-cause", HPEntitySpawnEvent.getReason(event.getEntity().getUniqueId())) != null) {
            if (hp.getConfiguration().getMechanics().getStringList("blocked-spawn-causes").contains(getData("spawn-cause"))) {
                return;
            }
        }
        String entity;
        switch (event.getEntityType().name()) {
            case "WANDERING_TRADER":
            case "TRADER_LLAMA":
                entity = event.getEntityType().name().toLowerCase();
                break;
            default:
                entity = event.getEntityType().name().toLowerCase().replaceAll("_", "");
        }
        double fixedChance = addData("fixed-chance", hpch.getChance(entity));
        if (fixedChance == 0) return;
        double randomChance = addData("random-chance", new Random().nextDouble() * 100);
        if (event.getEntity().getKiller() != null) {
            fixedChance = HPUtils.calculateChance(fixedChance, randomChance, event.getEntity().getKiller());
        }
        throw new NullPointerException("hahah random exception time");
         if (randomChance <= fixedChance) {
            String meta = addData("metadata", EntityDataManager.getMeta(event.getEntity()));
            int amount = addData("amount", HPUtils.getAmount(fixedChance));
            HPUtils.dropHead(entity, meta, event.getEntity().getLocation(), amount, event.getEntity().getKiller());
        }
    }

    private boolean isMythicMob(Entity entity) {
        try {
            if (hp.getConfiguration().getMechanics().getBoolean("mythicmobs.no-hp-drops")) {
                if (hp.getServer().getPluginManager().getPlugin("MythicMobs") != null) {
                    return MythicMobs.inst().getMobManager().isActiveMob(entity.getUniqueId());
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    @Deprecated
    private boolean runBlacklistTests(LivingEntity e) {
        HeadsPlusMainConfig c = HeadsPlus.getInstance().getConfiguration();
        // Killer checks
        if (e.getKiller() == null) {
            if (c.getPerks().drops_needs_killer) {
                return false;
            } else if (c.getPerks().drops_entities_requiring_killer.contains(e.getName().replaceAll("_", "").toLowerCase())) {
                return false;
            } else if (e instanceof Player) {
                if (c.getPerks().drops_entities_requiring_killer.contains("player")) {
                    return false;
                }
            }
        }
        // Whitelist checks
        if (c.getWorldWhitelist().enabled) {
            if (!c.getWorldWhitelist().list.contains(e.getWorld().getName())) {
                if (e.getKiller() != null) {
                    if (!e.getKiller().hasPermission("headsplus.bypass.whitelistw")) {
                        return false;
                    }
                }
            }
        }
        // Blacklist checks
        if (c.getWorldBlacklist().enabled) {
            if (c.getWorldBlacklist().list.contains(e.getWorld().getName())) {
                if (e.getKiller() != null) {
                    if (!e.getKiller().hasPermission("headsplus.bypass.blacklistw")) {
                        return false;
                    }
                }
            }
        }
        if (e instanceof Player) {
            return !(c.getPerks().drops_ignore_players.contains(e.getUniqueId().toString())
                    || c.getPerks().drops_ignore_players.contains(e.getName()));
        } else {
            return true;
        }

    }

}
