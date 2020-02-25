package io.github.thatsmusic99.headsplus.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.List;

public class SellHeadEvent extends Event implements Cancellable {

    // S
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private double totalPaid;
    private List<String> soldEntities;
    private final Player player;
    private double oldBalance;
    private double newBalance;
    private HashMap<String, Integer> entityAmounts;

    public SellHeadEvent(double tPrice, List<String> se, Player p, double o, double n, HashMap<String, Integer> s) {
        this.totalPaid = tPrice;
        this.soldEntities = se;
        this.player = p;
        this.oldBalance = o;
        this.newBalance = n;
        this.entityAmounts = s;
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

    public List<String> getSoldEntities() {
        return soldEntities;
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

    public void setSoldEntities(List<String> soldEntities) {
        this.soldEntities = soldEntities;
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
