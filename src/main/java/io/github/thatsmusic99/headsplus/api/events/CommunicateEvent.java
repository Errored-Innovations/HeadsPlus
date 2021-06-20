package io.github.thatsmusic99.headsplus.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Deprecated
public class CommunicateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final String plugin;

    public CommunicateEvent(String plugin) {
        this.plugin = plugin;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getPlugin() {
        return plugin;
    }
}
