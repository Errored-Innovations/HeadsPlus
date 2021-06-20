package io.github.thatsmusic99.headsplus.api.events;

import io.github.thatsmusic99.headsplus.api.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LevelUpEvent extends Event implements Cancellable {

    private final Player player;
    private final Level currentLevel;
    private final Level nextLevel;
    private boolean cancelled;

    public LevelUpEvent(Player player, Level c, Level n) {
        this.player = player;
        this.currentLevel = c;
        this.nextLevel = n;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public Level getNextLevel() {
        return nextLevel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
