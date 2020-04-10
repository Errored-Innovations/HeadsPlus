package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.HeadPurchaseEvent;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomHead extends Content {


    public CustomHead(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (event.isLeftClick()) { // Check if we're giving the head
            if (player.getInventory().firstEmpty() == -1) { // Check if there is a free space
                player.sendMessage(hpc.getString("commands.head.full-inv", player));
                return;
            }
            double price = player.hasPermission("headsplus.bypass.cost") ? 0 : NBTManager.getPrice(item); // Set price to 0 or not
            Economy ef = null;
            if (price > 0.0
                    && hp.econ()
                    && (ef = hp.getEconomy()) != null
                    && price > ef.getBalance(player)) { // If Vault is enabled, price is above 0, and the player can't afford the head
                player.sendMessage(hpc.getString("commands.heads.not-enough-money", player)); // K.O
                return;
            }
            HeadPurchaseEvent purchaseEvent = new HeadPurchaseEvent(player, item);
            Bukkit.getServer().getPluginManager().callEvent(purchaseEvent);
            if (!purchaseEvent.isCancelled()) {
                if(price > 0.0 && ef != null) {
                    EconomyResponse er = ef.withdrawPlayer(player, price);
                    if(er.transactionSuccess()) {
                        player.sendMessage(hpc.getString("commands.heads.buy-success", player)
                                .replaceAll("\\{price}", hp.getConfiguration().fixBalanceStr(price))
                                .replaceAll("\\{balance}", Double.toString(ef.getBalance(player))));
                    } else {
                        player.sendMessage(hpc.getString("commands.errors.cmd-fail", player) + ": " + er.errorMessage);
                        return;
                    }
                }
                if (player.getInventory().firstEmpty() == 8) {
                    InventoryManager im = InventoryManager.getManager(player);
                    if (im != null) {
                        im.setGlitchSlot(true);
                    }
                }
                player.getInventory().addItem(NBTManager.removeIcon(item));
            }
        } else {
            HPPlayer hpp = HPPlayer.getHPPlayer(player);
            ItemMeta im2 = item.getItemMeta();
            String price = String.valueOf(NBTManager.getPrice(item));
            String id = NBTManager.getID(event.getCurrentItem());
            List<String> s = new ArrayList<>();
            if (hpp.hasHeadFavourited(id)) {
                hpp.removeFavourite(id);
            } else {
                hpp.addFavourite(id);
            }
            for (String r : getLore()) {
                s.add(ChatColor.translateAlternateColorCodes('&', r.replaceAll("\\{price}", price)
                        .replaceAll("\\{favourite}", hpp.hasHeadFavourited(id) ? ChatColor.GOLD + "Favourite!" : "")));
            }
            im2.setLore(s);
            item.setItemMeta(im2);
            event.getInventory().setItem(event.getSlot(), item);
        }
    }

    @Override
    public String getId() {
        return "custom-head";
    }


}
