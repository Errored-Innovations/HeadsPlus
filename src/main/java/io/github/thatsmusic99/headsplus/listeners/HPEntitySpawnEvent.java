package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.WeakHashMap;

public class HPEntitySpawnEvent extends HeadsPlusListener<CreatureSpawnEvent> {

    private static final WeakHashMap<UUID, CreatureSpawnEvent.SpawnReason> spawnTracker = new HashMap<>();

    public HPEntitySpawnEvent() {
        super();
        Bukkit.getPluginManager().registerEvent(CreatureSpawnEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(CreatureSpawnEvent.class, "EntitySpawnEvent"), HeadsPlus.getInstance());
    }

    @Override
    public void onEvent(CreatureSpawnEvent event) {
        spawnTracker.put(event.getEntity().getUniqueId(), event.getSpawnReason());
    }

    public static CreatureSpawnEvent.SpawnReason getReason(UUID uuid) {
        return spawnTracker.get(uuid);
    }
}
