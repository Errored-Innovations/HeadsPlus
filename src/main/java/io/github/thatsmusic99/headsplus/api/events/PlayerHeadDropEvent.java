package io.github.thatsmusic99.headsplus.api.events;

import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerHeadDropEvent extends EntityHeadDropEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Player deadPlayer;

    public PlayerHeadDropEvent(Player deadPlayer, Player killer, EntityDataManager.DroppedHeadInfo head, Location location, int amount) {
        super(killer, head, location, EntityType.PLAYER, amount);
        this.deadPlayer = deadPlayer;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getDeadPlayer() {
        return deadPlayer;
    }
}
