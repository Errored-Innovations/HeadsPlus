package io.github.thatsmusic99.headsplus.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class HeadPurchaseEvent extends Event implements Cancellable {

    // E
	private final Player player;
	private final ItemStack itemStack;
	private boolean cancelled = false;

    public HeadPurchaseEvent(Player player, ItemStack itemStack) {
        this.player = player;
        this.itemStack = itemStack;
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
    
    public ItemStack getItemStack() {
    	return this.itemStack;
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
