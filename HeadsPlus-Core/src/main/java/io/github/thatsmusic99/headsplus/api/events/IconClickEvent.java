package io.github.thatsmusic99.headsplus.api.events;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class IconClickEvent extends Event implements Cancellable {

    private final UUID player;
    private final Icon icon;
    private boolean cancelled = false;
    private boolean destroy = true;

    public IconClickEvent(Player player, Icon icon) {
        this.player = player.getUniqueId();
        this.icon = icon;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.player);
    }

    public Icon getItemStack() {
        return this.icon;
    }

    public boolean willDestroy() {
        return destroy;
    }

    public void setToDestroy(boolean destroy) {
        this.destroy = destroy;
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
