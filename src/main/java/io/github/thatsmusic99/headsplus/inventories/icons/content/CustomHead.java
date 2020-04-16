package io.github.thatsmusic99.headsplus.inventories.icons.content;

import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.HeadPurchaseEvent;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomHead extends Content {

    private double price;
    private String id;

    public CustomHead(String id) {
        super(hp.getHeadsXConfig().getSkull(id));
        this.price = hp.getHeadsXConfig().getPrice(id);
        this.id = id;
    }

    public CustomHead() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem().clone();
        if (event.isLeftClick()) { // Check if we're giving the head
            if (player.getInventory().firstEmpty() == -1) { // Check if there is a free space
                player.sendMessage(hpc.getString("commands.head.full-inv", player));
                return false;
            }
            double price = player.hasPermission("headsplus.bypass.cost") ? 0 : NBTManager.getPrice(item); // Set price to 0 or not
            Economy ef = null;
            if (price > 0.0
                    && hp.econ()
                    && (ef = hp.getEconomy()) != null
                    && price > ef.getBalance(player)) { // If Vault is enabled, price is above 0, and the player can't afford the head
                player.sendMessage(hpc.getString("commands.heads.not-enough-money", player)); // K.O
                return false;
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
                        return false;
                    }
                }
                if (player.getInventory().firstEmpty() == 8) {
                    InventoryManager im = InventoryManager.getManager(player);
                    im.setGlitchSlot(true);
                }
                ItemMeta meta = item.getItemMeta();
                meta.setLore(new ArrayList<>());
                item.setItemMeta(meta);
                player.getInventory().addItem(item);
            }
        } else {
            HPPlayer hpp = HPPlayer.getHPPlayer(player);
            if (hpp.hasHeadFavourited(id)) {
                hpp.removeFavourite(id);
            } else {
                hpp.addFavourite(id);
            }
            initNameAndLore(id, player);
            event.getInventory().setItem(event.getSlot(), this.item);
        }
        return false;
    }

    @Override
    public String getId() {
        return "head";
    }

    @Override
    public void initNameAndLore(String id, Player player) {
        // We only really need to add the lore here
        List<String> lore = new ArrayList<>();
        for (String str : hpi.getStringList("icons.head.lore")) {
            if (str.contains("{favourite}") || str.contains("{msg_inventory.icon.head.favourite}")) {
                if (HPPlayer.getHPPlayer(player).hasHeadFavourited(id)) {
                    lore.add(hpc.getString("inventory.icon.head.favourite", player));
                }
            } else {
                lore.add(hpc.formatMsg(str, player).replaceAll("\\{price}", String.valueOf(price)));
            }

        }
        ItemMeta im = item.getItemMeta();
        im.setLore(lore);
        item.setItemMeta(im);
    }

    @Override
    public String getDefaultDisplayName() {
        return "{head-name}";
    }

    @Override
    public String[] getDefaultLore() {
        return new String[]{"{msg_inventory.icon.head.price}", "{msg_inventory.icon.head.favourite}"};
    }
}
