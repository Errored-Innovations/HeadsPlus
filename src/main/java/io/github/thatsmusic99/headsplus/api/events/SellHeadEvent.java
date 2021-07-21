package io.github.thatsmusic99.headsplus.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;

public class SellHeadEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private double totalPaid;
    private final Player player;
    private double oldBalance;
    private double newBalance;
    private HashMap<String, Integer> entityAmounts;

    public SellHeadEvent(double price, Player player, double oldBal, double newBal, HashMap<String, Integer> entities) {
        this.totalPaid = price;
        this.player = player;
        this.oldBalance = oldBal;
        this.newBalance = newBal;
        this.entityAmounts = entities;
    }

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

    public Player getPlayer() {
        return player;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    public void setOldBalance(double oldBalance) {
        this.oldBalance = oldBalance;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    public HashMap<String, Integer> getEntityAmounts() {
        return entityAmounts;
    }

    public void setEntityAmounts(HashMap<String, Integer> entityAmounts) {
        this.entityAmounts = entityAmounts;
    }
}
