package io.github.thatsmusic99.headsplus.crafting;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HeadsPlusAPI;
import io.github.thatsmusic99.headsplus.api.events.HeadCraftEvent;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.FlagHandler;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.meta.SkullMeta;

public class RecipePerms extends HeadsPlusListener<InventoryClickEvent> {

    public RecipePerms() {
        Bukkit.getPluginManager().registerEvent(InventoryClickEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(InventoryClickEvent.class, "RecipeHandlingEvent", this), HeadsPlus.getInstance());
    }
	public void onEvent(InventoryClickEvent e) {
        addData("player", e.getWhoClicked().getName());
        addData("inventory-type", e.getInventory().getType().name());
        addData("slot", e.getRawSlot());

        Player player = (Player) e.getWhoClicked();
        HeadsPlus hp = HeadsPlus.getInstance();
        HeadsPlusMainConfig c = hp.getConfiguration();
        if (e.getInventory().getType().equals(InventoryType.CRAFTING) || e.getInventory().getType().equals(InventoryType.WORKBENCH)) {
            if (addData("is-correct-slot", isValid1_14(e))) {
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().getItemMeta() instanceof SkullMeta) {
                        String type = addData("type", NBTManager.getType(e.getCurrentItem()));
                        if (type != null && !type.isEmpty()) {
                            // Beyond this point, we can start denying events
                            if (addData("enabled", c.getPerks().craft_heads)) {
                                if (addData("has-permission", player.hasPermission("headsplus.craft"))) {
                                    if (addData("has-worldguard", Bukkit.getPluginManager().isPluginEnabled("WorldGuard"))) {
                                        addData("skull-type", type);
                                        if (!FlagHandler.canCraft(e.getWhoClicked().getLocation(), EntityType.valueOf(type))) {
                                            denyPermission(e);
                                            return;
                                        }
                                    }

                                    if (c.getWorldBlacklist().enabled) {
                                        if (!c.getWorldBlacklist().list.contains(player.getWorld().getName())
                                                || player.hasPermission("headsplus.bypass.blacklistw")) {

                                            if (c.getWorldWhitelist().list.contains(player.getWorld().getName())) {
                                                fireEvent(e);
                                                return;
                                            } else if (player.hasPermission("headsplus.bypass.whitelistw")){
                                                try {
                                                    fireEvent(e);
                                                } catch (NullPointerException | ClassCastException ignored) {
                                                }
                                            } else if (!c.getWorldWhitelist().enabled) {
                                                fireEvent(e);
                                                return;
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                            denyPermission(e);
                        }
                    }
                }
            }
        }
	}

	private int shift(InventoryClickEvent e) {
	    int amount;
        if (e.isShiftClick()) {
            int a = 0;
            if (e.getInventory().getType().equals(InventoryType.WORKBENCH)) {
                for (int i = 1; i <= 9; i++) {
                    if (e.getInventory().getItem(i) != null) {
                        a += e.getInventory().getItem(i).getAmount();
                    }
                }
            } else {
                for (int i = 80; i <= 83; i++) {
                    if (e.getInventory().getItem(i) != null) {
                        a += e.getInventory().getItem(i).getAmount();
                    }
                }
            }
            if (a % 2 == 0) {
                amount = a / 2;
            } else {
                amount = (a - 1) / 2;
            }
        } else {
            amount = 1;
        }
        return amount;
    }

    private void fireEvent(InventoryClickEvent e) {
        HeadsPlus hp = HeadsPlus.getInstance();
        HeadCraftEvent event;
        HeadsPlusAPI hapi = hp.getAPI();
        int amount = shift(e);
        event = new HeadCraftEvent((Player) e.getWhoClicked(), e.getCurrentItem(), e.getWhoClicked().getWorld(), e.getWhoClicked().getLocation(), amount, hapi.getSkullType(e.getCurrentItem()));
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            e.setCurrentItem(NBTManager.makeSellable(e.getCurrentItem()));
            e.setCurrentItem(NBTManager.setType(e.getCurrentItem(), hapi.getSkullType(e.getCurrentItem())));
        } else {
            e.setCancelled(true);
        }
    }

    private void denyPermission(InventoryClickEvent e) {
        if(e.getRawSlot() == 0){
            if(!NBTManager.getType(e.getCurrentItem()).isEmpty()){
                e.getWhoClicked().sendMessage(ChatColor.RED + "You can not craft heads!");
                e.setCancelled(true);
            }
        }
    }

    private boolean isValid1_14(InventoryClickEvent e) {
	    if (HeadsPlus.getInstance().getNMSVersion().getOrder() < 11) return true;
	    if (e.getInventory().getType().equals(InventoryType.WORKBENCH)) {
	        return e.getRawSlot() == 0;
        }
	    if (e.getInventory().getType().equals(InventoryType.CRAFTING)) {
	        if (e.getWhoClicked().getGameMode() == GameMode.SURVIVAL) {
                return e.getRawSlot() == 0;
            }
        }
        return false;
    }
}
