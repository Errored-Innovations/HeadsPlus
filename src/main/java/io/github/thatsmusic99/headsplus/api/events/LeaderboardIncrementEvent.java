package io.github.thatsmusic99.headsplus.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeaderboardIncrementEvent extends Event implements Cancellable{

    // T
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private Player player;
    private String type;
    private int oldValue;
    private int newValue;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public LeaderboardIncrementEvent(Player p, String type, int old, int n3w) {
        player = p;
        this.type = type;
        oldValue = old;
        newValue = n3w;
    }

    public Player getPlayer() {
        return player;
    }

    public int getNewValue() {
        return newValue;
    }

    public int getOldValue() {
        return oldValue;
    }

    public String getType() {
        return type;
    }

}
