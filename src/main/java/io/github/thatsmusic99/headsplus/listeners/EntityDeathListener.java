package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.EntityHeadDropEvent;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.managers.RestrictionsManager;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EntityDeathListener extends HeadsPlusListener<EntityDeathEvent> {

    @Override
    public void onEvent(EntityDeathEvent event) {
        addData("entity-type", event.getEntityType().name());
        addData("killer", event.getEntity().getKiller() == null ? "<None>" : event.getEntity().getKiller().getName());
        // Make sure the entity is valid
        if (!EntityDataManager.ableEntities.contains(event.getEntityType().name())) return;
        // Make sure the entity isn't from MythicMobs
        if (addData("is-mythic-mob", HPUtils.isMythicMob(event.getEntity()))) return;
        // And make sure there is no WG region saying no
        // I SWEAR TO GOD WORLDGUARD IS SUCH A BRAT
        if (!addData("not-wg-restricted",
                !HeadsPlus.get().canUseWG() || FlagHandler.canDrop(event.getEntity().getLocation(),
                        event.getEntity().getType().toString())))
            return;

        if (!shouldDropHead(event.getEntity())) return;
        //
        if (addData("spawn-cause", EntitySpawnListener.getReason(event.getEntity().getUniqueId())) != null) {
            if (MainConfig.get().getMobDrops().BLOCKED_SPAWN_CAUSES.contains(getData("spawn-cause"))) {
                return;
            }
        }
        String entity = event.getEntityType().name();
        // Check for each head
        String meta = addData("metadata", EntityDataManager.getMeta(event.getEntity()));
        List<EntityDataManager.DroppedHeadInfo> heads = EntityDataManager.getStoredHeads().get(entity + ";" + meta);
        String chosenConditions = meta;
        if (heads == null) {
            String[] possibleConditions = meta.split(",");
            for (String str : possibleConditions) {
                chosenConditions = str;
                if ((heads = EntityDataManager.getStoredHeads().get(entity + ";" + str)) != null) break;
            }
            if (heads == null) {
                chosenConditions = "default";
                heads = EntityDataManager.getStoredHeads().get(entity + ";default");
            }
        }
        if (heads == null) {
            throw new NullPointerException("Found no heads list for " + entity + "!");
        }

        // Check the chance of each head
        // TODO - option to set a max number of heads to drop at once
        for (EntityDataManager.DroppedHeadInfo info : heads) {
            double fixedChance = addData("fixed-chance", info.getChance());
            if (fixedChance == 0) return;
            double randomChance = addData("random-chance", new Random().nextDouble() * 100);
            if (event.getEntity().getKiller() != null && !MainConfig.get().getMobDrops().LOOTING_IGNORED.contains(entity)) {
                fixedChance = HPUtils.calculateChance(fixedChance, randomChance, event.getEntity().getKiller());
            }

            if (randomChance <= fixedChance) {
                int amount = addData("amount", HPUtils.getAmount(fixedChance));

                // Drop the head itself
                dropHead(entity, chosenConditions, info, event.getEntity().getLocation(), amount,
                        event.getEntity().getKiller());
            }
        }
    }

    private boolean shouldDropHead(LivingEntity entity) {
        String entityName = entity.getType().name();
        // Check world restrictions
        if (!RestrictionsManager.canUse(entity.getWorld().getName(), RestrictionsManager.ActionType.MOBS)) return false;
        //
        if (entity.getKiller() == null) {
            if (MainConfig.get().getMobDrops().NEEDS_KILLER) return false;
            if (MainConfig.get().getMobDrops().ENTITIES_NEEDING_KILLER.contains(entityName)) return false;
        } else {
            if (!entity.getKiller().hasPermission("headsplus.drops." + entityName.toLowerCase())) return false;
        }
        return true;
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(EntityDeathEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(EntityDeathEvent.class, "EntityDeathEvent", this), HeadsPlus.get(), true);

        addPossibleData("entity-type", EntityDataManager.ableEntities.toArray(new String[]{}));

        for (String key : Arrays.asList("enabled", "is-mythic-mob", "not-wg-restricted")) {
            addPossibleData(key, "true", "false");
        }

        int length = CreatureSpawnEvent.SpawnReason.values().length;
        String[] reasons = new String[length];
        for (int i = 0; i < length; i++) {
            reasons[i] = CreatureSpawnEvent.SpawnReason.values()[i].name();
        }
        addPossibleData("spawn-cause", reasons);

        addPossibleData("fixed-chance", "<double>");
        addPossibleData("random-chance", "<double>");
        List<String> metadata = new ArrayList<>();
        for (Horse.Color color : Horse.Color.values()) {
            HPUtils.addIfAbsent(metadata, color.name());
        }
        for (DyeColor color : DyeColor.values()) {
            HPUtils.addIfAbsent(metadata, color.name());
        }
        for (Rabbit.Type type : Rabbit.Type.values()) {
            HPUtils.addIfAbsent(metadata, type.name());
        }
        for (TropicalFish.Pattern pattern : TropicalFish.Pattern.values()) {
            HPUtils.addIfAbsent(metadata, pattern.name());
        }
        metadata.add("SNOW");
        for (Cat.Type type : Cat.Type.values()) {
            HPUtils.addIfAbsent(metadata, type.name());
        }


        addPossibleData("metadata", "default",
                "WHITE", "CREAMY", "CHESTNUT", "BROWN", "BLACK", "GRAY", "DARK_BROWN",
                "RED", "ORANGE", "YELLOW", "LIME", "GREEN", "LIGHT_BLUE", "CYAN", "BLUE", "PURPLE", "MAGENTA", "PINK"
                , "LIGHT_GRAY",
                "BLACK_AND_WHITE", "GOLD", "SALT_AND_PEPPER", "THE_KILLER_BUNNY",
                "KOB", "SUNSTREAK", "SNOOPER", "DASHER", "BRINELY", "SPOTTY", "FLOPPER", "STRIPEY", "GLITTER",
                "BLOCKFISH", "BETTY", "CLAYFISH",
                "SNOW",
                "TABBY", "SIAMESE", "BRITISH_SHORTHAIR", "CALICO", "PERSIAN", "RAGDOLL", "JELLIE", "ALL_BLACK",
                "NONE", "");

        addPossibleData("killer", "<Player>");
    }

    public static void dropHead(String id, String conditions, EntityDataManager.DroppedHeadInfo info,
                                Location location, int amount, Player killer) {

        EntityHeadDropEvent event = new EntityHeadDropEvent(killer, info, location, EntityType.valueOf(id), amount);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {

            // Run commands on drop
            ConfigMobs.get().getDropCommands(id).forEach(command -> {

                // Replace placeholders
                command = command.replaceAll("\\{player}", killer.getName()).replaceAll("\\{entity}", id);

                // Check syntax
                if (command.startsWith("player:")) {
                    killer.performCommand(command.replaceFirst("player:", ""));
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            });

            info.buildHead().thenAccept(head -> {
                // Because I need to set up extra l o r e
                ItemMeta meta = head.getItemMeta();
                meta.setLore(ConfigMobs.get().getLore(id, conditions, info.getId(), info.getPrice(), killer == null ? null : killer.getName()));
                head.setItemMeta(meta);

                head.setAmount(amount);
                PersistenceManager.get().setSellable(head, true);
                PersistenceManager.get().setSellType(head, "mobs_" + id + ":" + conditions + ":" + info.getId());
                location.getWorld().dropItem(location, head);
            });
        }
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().MOB_DROPS;
    }
}
