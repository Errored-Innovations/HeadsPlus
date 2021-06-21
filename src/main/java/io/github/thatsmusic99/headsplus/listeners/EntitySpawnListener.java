package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.UUID;
import java.util.WeakHashMap;

public class EntitySpawnListener extends HeadsPlusListener<CreatureSpawnEvent> {

    private static final WeakHashMap<UUID, CreatureSpawnEvent.SpawnReason> spawnTracker = new WeakHashMap<>();

    @Override
    public void onEvent(CreatureSpawnEvent event) {
        addData("entity-type", event.getEntity().getType());
        addData("entity-uuid", event.getEntity().getUniqueId().toString());
        addData("spawn-reason", event.getSpawnReason().name());
        spawnTracker.put(event.getEntity().getUniqueId(), event.getSpawnReason());
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(CreatureSpawnEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(CreatureSpawnEvent.class, "CreatureSpawnEvent", this), HeadsPlus.get(), true);
        String[] entities = new String[]{};
        entities = EntityDataManager.ableEntities.toArray(entities);

        // Adds possible values for verbose usage
        addPossibleData("entity-type", entities);
        addPossibleData("entity-uuid");
        int length = CreatureSpawnEvent.SpawnReason.values().length;
        String[] reasons = new String[length];
        for (int i = 0; i < length; i++) {
            reasons[i] = CreatureSpawnEvent.SpawnReason.values()[i].name();
        }
        addPossibleData("spawn-reason", reasons);
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().MOB_DROPS;
    }

    public static CreatureSpawnEvent.SpawnReason getReason(UUID uuid) {
        return spawnTracker.get(uuid);
    }
}
