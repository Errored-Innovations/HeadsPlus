package io.github.thatsmusic99.headsplus.api.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerHeadDropEvent extends Event implements Cancellable {

    // M
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Player deadPlayer;
    private final Player killer;
    private final ItemStack head;
    private final World world;
    private final Location location;

    public PlayerHeadDropEvent(Player deadPlayer, Player killer, ItemStack head, World world, Location location) {
        this.deadPlayer = deadPlayer;
        this.killer = killer;
        this.head = head;
        this.world = world;
        this.location = location;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public Player getDeadPlayer() {
        return deadPlayer;
    }

    public Player getKiller() {
        return killer;
    }

    public ItemStack getSkull() {
        return head;
    }

    public World getWorld() {
        return world;
    }

    public Location getLocation() {
        return location;
    }

}
