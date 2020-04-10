package io.github.thatsmusic99.headsplus.config.customheads.icons;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.api.events.HeadPurchaseEvent;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.customheads.Icon;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import io.github.thatsmusic99.headsplus.util.InventoryManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Head extends ItemStack implements Icon {

    private final HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();
    @Override
    public String getIconName() {
        return "head";
    }

    @Override
    public void onClick(Player p, InventoryManager im, InventoryClickEvent e) {
        e.setCancelled(true);
        NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
        if (im.getType() == InventoryManager.Type.SELL) {
            if (!nbt.getType(e.getCurrentItem()).isEmpty()) {
                if (e.isRightClick()) {
                    p.performCommand("sellhead " + nbt.getType(e.getCurrentItem()) + " 1");
                } else {
                    p.performCommand("sellhead " + nbt.getType(e.getCurrentItem()));
                }

            }
        } else {
            if (e.getClick().isRightClick()) {
                HPPlayer hpp = HPPlayer.getHPPlayer(p);
                String id = nbt.getID(e.getCurrentItem());
                ItemMeta im2 = e.getCurrentItem().getItemMeta();
                List<String> s = new ArrayList<>();
                if (hpp.hasHeadFavourited(id)) {
                    hpp.removeFavourite(id);
                    for (String r : getLore()) {
                        s.add(ChatColor.translateAlternateColorCodes('&', r.replaceAll("\\{price}", String.valueOf(nbt.getPrice(e.getCurrentItem())))
                                .replaceAll("\\{favourite}", HPPlayer.getHPPlayer(p).hasHeadFavourited(nbt.getID(e.getCurrentItem())) ? ChatColor.GOLD + "Favourite!" : "")));

                    }
                } else {
                    hpp.addFavourite(id);
                    for (String r : getLore()) {
                        s.add(ChatColor.translateAlternateColorCodes('&', r.replaceAll("\\{price}", String.valueOf(nbt.getPrice(e.getCurrentItem())))
                                .replaceAll("\\{favourite}", HPPlayer.getHPPlayer(p).hasHeadFavourited(nbt.getID(e.getCurrentItem())) ? ChatColor.GOLD + "Favourite!" : "")));

                    }
                }
                im2.setLore(s);
                e.getCurrentItem().setItemMeta(im2);
                e.getInventory().setItem(e.getSlot(), e.getCurrentItem());
            } else {
                giveHead(p, e);
            }
        }

    }

    private void giveHead(Player p, InventoryClickEvent e) {
        NBTManager nbt = HeadsPlus.getInstance().getNBTManager();
        if (p.getInventory().firstEmpty() == -1) {
            p.sendMessage(hpc.getString("commands.head.full-inv", p));
            return;
        }
        double price = p.hasPermission("headsplus.bypass.cost") ? 0 : nbt.getPrice(e.getCurrentItem());
        Economy ef = null;
		if (price > 0.0
                && HeadsPlus.getInstance().econ()
                && (ef = HeadsPlus.getInstance().getEconomy()) != null
                && price > ef.getBalance(p)) {
            p.sendMessage(hpc.getString("commands.heads.not-enough-money", p));
            return;
        }
        HeadPurchaseEvent event = new HeadPurchaseEvent(p, e.getCurrentItem());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            boolean ok = true;
            if(price > 0.0 && ef != null) {
                EconomyResponse er = ef.withdrawPlayer(p, price);
                if((ok = er.transactionSuccess())) {
                    p.sendMessage(hpc.getString("commands.heads.buy-success", p)
                        .replaceAll("\\{price}", HeadsPlus.getInstance().getConfiguration().fixBalanceStr(price))
                        .replaceAll("\\{balance}", Double.toString(ef.getBalance(p))));
                } else {
                    p.sendMessage(hpc.getString("commands.errors.cmd-fail", p) + ": " + er.errorMessage);
                }
            }
            if (ok) {
                if (p.getInventory().firstEmpty() == 8) {
                    InventoryManager im = InventoryManager.get(p);
                    if (im != null) {
                        im.setGlitchSlotFilled(true);
                    }
                }
                p.getInventory().addItem(HeadsPlus.getInstance().getNBTManager().removeIcon(e.getCurrentItem()));
            }
        }
    }

    @Override
    public Material getDefaultMaterial() {
        return HeadsPlus.getInstance().getNMS().getSkullMaterial(1).getType();
    }

    @Override
    public List<String> getDefaultLore() {
        return new ArrayList<>(Arrays.asList("{msg_inventory.icon.head.price}", "{msg_inventory.icon.head.favourite}"));
    }

    @Override
    public String getDefaultDisplayName() {
        return "{head-name}";
    }

    @Override
    public Icon getReplacementIcon() {
        return new Air();
    }

    @Override
    public List<String> getLore() {
        return HeadsPlus.getInstance().getItems().getConfig().getStringList("icons." + getIconName() + ".lore");
    }

    @Override
    public String getSingleLetter() {
        return "H";
    }
}
