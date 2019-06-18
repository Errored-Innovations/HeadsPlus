package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.headsx.Icon;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryEvent implements Listener {

    @EventHandler
    public void onClickEvent(InventoryClickEvent e) {
        try {
            if (!(e.getWhoClicked() instanceof Player)) return;
            Player p = (Player) e.getWhoClicked();
            if (e.getRawSlot() > 53) return;
            if (InventoryManager.get(p) == null) return;
            final InventoryManager im = InventoryManager.get(p);
            NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
            // int month = Calendar.getInstance().get(Calendar.MONTH);
            if (im.getType().name().startsWith("LIST")) {
                Icon i = nbt.getIcon(e.getCurrentItem());
                if (i == null) return;
                if (e.getRawSlot() < 54) {
                    e.setCancelled(true);
                }
                i.onClick(p, im, e);
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
