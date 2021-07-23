package io.github.thatsmusic99.headsplus.api.events;

import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.managers.HeadManager;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityHeadDropEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private EntityDataManager.DroppedHeadInfo head;
    private Player player;
    private EntityType entityType;
    private Location location;
    private final int amount;

    public EntityHeadDropEvent(Player killer, EntityDataManager.DroppedHeadInfo head, Location location, EntityType entityType, int amount) {
        this.player = killer;
        this.head = head;
        this.location = location;
        this.entityType = entityType;
        this.amount = amount;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public EntityDataManager.DroppedHeadInfo getHeadInfo() {
        return head;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setHeadInfo(EntityDataManager.DroppedHeadInfo skull) {
        this.head = skull;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String getEventName() {
        return super.getEventName();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
