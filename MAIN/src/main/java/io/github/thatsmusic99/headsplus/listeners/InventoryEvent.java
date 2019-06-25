package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.events.HeadPurchaseEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.customheads.Icon;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryEvent implements Listener {

    @EventHandler
    public void onClickEvent(InventoryClickEvent e) {
        try {

            if (!(e.getWhoClicked() instanceof Player)) return; // If the user who clicked is a player

            Player p = (Player) e.getWhoClicked(); // Get the player

            if (e.getRawSlot() > 53) return; // If the raw slot is above 53 (i.e. the player's inventory)

            if (InventoryManager.get(p) == null) return; // See if the player has any kind of inventory open

            final InventoryManager im = InventoryManager.get(p); // If so, get that inventory

            if (im.getInventory() == null) return; // Not having this line caused another bug

            NBTManager nbt = HeadsPlus.getInstance().getNBTManager(); // NBT/Reflection
            // <Old Christmas code>
            // int month = Calendar.getInstance().get(Calendar.MONTH);
            if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) { // If the item is moved to another inventory,
                e.setCancelled(true); // Cancel the event.
                return; // Stop
            }
            if (e.getClick().isShiftClick()) { // If it's a shift click...
                e.setCancelled(true); // Cancel the event.
                return; // Stop
            }
            // Different inventory types that return true:
            // LIST_MENU
            // LIST_CATEGORY
            // LIST_SEARCH
            // LIST_FAVORITES

            // Basically, all from /heads
            if (im.getType().name().startsWith("LIST")) {
                Icon i = nbt.getIcon(e.getCurrentItem()); // Get the icon (in this case, head)
                if (i == null) return; // If it's null, stop.
                if (e.getRawSlot() < 54) { // If the raw slot is below 54 (inside the selector), cancel the event
                    e.setCancelled(true);
                }
                i.onClick(p, im, e); //
            } else {
                Icon i = nbt.getIcon(e.getCurrentItem());
                if (i == null) return;
                i.onClick(p, im, e);
            }
        } catch (Exception ex) {
            DebugPrint.createReport(ex, "Event (InventoryInteractEvent)", false, null);
        }
    }
	
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        try {
			if(!(e.getPlayer() instanceof Player)) return;
			InventoryManager.inventoryClosed((Player) e.getPlayer());
		} catch (Exception ex) {
            DebugPrint.createReport(ex, "Event (InventoryCloseEvent)", false, null);
        }
	}
}
