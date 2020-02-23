package io.github.thatsmusic99.headsplus.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SectionChangeEvent extends Event implements Cancellable {

    // E
    private final Player player;
    private final String newSection;
    private final String oldSection;
    private boolean cancelled = false;

    public SectionChangeEvent(Player player, String newSection, String oldSection) {
        this.player = player;
        this.newSection = newSection;
        this.oldSection = oldSection;
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

    public String getNewSection() {
        return newSection;
    }

    public String getOldSection() {
        return oldSection;
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
