package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.ConfigMobs;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.HPUtils;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.TropicalFish;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;

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
        if (!addData("not-wg-restricted", !hp.canUseWG() || FlagHandler.canDrop(event.getEntity().getLocation(), event.getEntity().getType()))) return;
        // TODO New blacklist checks go here
        if (!HPUtils.runBlacklistTests(event.getEntity())) return;
        //
        if (addData("spawn-cause", EntitySpawnListener.getReason(event.getEntity().getUniqueId())) != null) {
            if (MainConfig.get().getMobDrops().BLOCKED_SPAWN_CAUSES.contains(getData("spawn-cause"))) {
                return;
            }
        }
        String entity = event.getEntityType().name();
        double fixedChance = addData("fixed-chance", ConfigMobs.get().getChance(entity));
        if (fixedChance == 0) return;
        double randomChance = addData("random-chance", new Random().nextDouble() * 100);
        if (event.getEntity().getKiller() != null && !MainConfig.get().getMobDrops().LOOTING_IGNORED.contains(entity)) {
            fixedChance = HPUtils.calculateChance(fixedChance, randomChance, event.getEntity().getKiller());
        }
        if (randomChance <= fixedChance) {
            String meta = addData("metadata", EntityDataManager.getMeta(event.getEntity()));
            int amount = addData("amount", HPUtils.getAmount(fixedChance));
            HPUtils.dropHead(entity, meta, event.getEntity().getLocation(), amount, event.getEntity().getKiller());
        }
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(EntityDeathEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(EntityDeathEvent.class, "EntityDeathEvent", this), HeadsPlus.get(), true);

        int length = EntityDataManager.ableEntities.size();
        String[] entities = new String[length];
        for (int i = 0; i < length; i++) {
            entities[i] = EntityDataManager.ableEntities.get(i);
        }
        addPossibleData("entity-type", entities);

        for (String key : Arrays.asList("enabled", "is-mythic-mob", "not-wg-restricted")) {
            addPossibleData(key, "true", "false");
        }

        length = CreatureSpawnEvent.SpawnReason.values().length;
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
                "RED", "ORANGE", "YELLOW", "LIME", "GREEN", "LIGHT_BLUE", "CYAN", "BLUE", "PURPLE", "MAGENTA", "PINK", "LIGHT_GRAY",
                "BLACK_AND_WHITE", "GOLD", "SALT_AND_PEPPER", "THE_KILLER_BUNNY",
                "KOB", "SUNSTREAK", "SNOOPER", "DASHER", "BRINELY", "SPOTTY", "FLOPPER", "STRIPEY", "GLITTER", "BLOCKFISH", "BETTY", "CLAYFISH",
                "SNOW",
                "TABBY", "SIAMESE", "BRITISH_SHORTHAIR", "CALICO", "PERSIAN", "RAGDOLL", "JELLIE", "ALL_BLACK",
                "NONE", "");

        addPossibleData("killer", "<Player>");
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().MOB_DROPS;
    }
}
