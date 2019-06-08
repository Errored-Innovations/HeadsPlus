package io.github.thatsmusic99.headsplus.api.events;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class EntityHeadDropEvent extends Event implements Cancellable {

    // O
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private ItemStack skull;
    private Player player;
    private EntityType entityType;
    private World world;
    private Location location;

    public EntityHeadDropEvent(Player killer, ItemStack head, World world, Location location, EntityType entityType) {
        this.player = killer;
        this.skull = head;
        this.world = world;
        this.location = location;
        this.entityType = entityType;
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

    public ItemStack getSkull() {
        return skull;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getSkullKey() {
        return HeadsPlus.getInstance().getNBTManager().getType(skull);
    }

    public Location getLocation() {
        return location;
    }
    public World getWorld() {
        return world;
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

    public void setSkull(ItemStack skull) {
        this.skull = skull;
    }

    public void setWorld(World world) {
        this.world = world;
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
