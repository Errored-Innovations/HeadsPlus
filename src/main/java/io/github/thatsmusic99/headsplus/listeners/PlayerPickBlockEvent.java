package io.github.thatsmusic99.headsplus.listeners;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.UUID;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.reflection.ProfileFetcher;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerPickBlockEvent extends HeadsPlusListener<InventoryCreativeEvent> implements Listener {

    HashSet<UUID> openInventories = new HashSet<>();

    public PlayerPickBlockEvent() {
        super();
        HeadsPlusListener<?> listener;
        Bukkit.getPluginManager().registerEvent(InventoryCreativeEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(InventoryCreativeEvent.class, "InventoryCreativeEvent", this), HeadsPlus.getInstance());

        Bukkit.getPluginManager().registerEvent(InventoryOpenEvent.class, listener = new HeadsPlusListener<InventoryOpenEvent>() {
            @Override
            public void onEvent(InventoryOpenEvent event) {
                if (event.getInventory().getType() == InventoryType.CREATIVE) {
                    openInventories.add(event.getPlayer().getUniqueId());
                }
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(InventoryOpenEvent.class, "InventoryOpenEvent", listener), HeadsPlus.getInstance());

        Bukkit.getPluginManager().registerEvent(InventoryCloseEvent.class, listener = new HeadsPlusListener<InventoryCloseEvent>() {
            @Override
            public void onEvent(InventoryCloseEvent event) {
                openInventories.remove(event.getPlayer().getUniqueId());
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(InventoryCloseEvent.class, "InventoryCloseEvent", listener), HeadsPlus.getInstance());
    }

    public void onEvent(InventoryCreativeEvent event) {
        if (event.getAction() == InventoryAction.PLACE_ALL // this is weird, but ok
                && !openInventories.contains(event.getWhoClicked().getUniqueId())
                && event.getCursor().getItemMeta() instanceof SkullMeta) {
            // Block pick event is basically the same event as picking a block from inventory
            // check to see if they are looking at a skull block
            Block b = event.getWhoClicked().getTargetBlock(null, 6);
            if (!(b.getState() instanceof Skull)) return;
            // fill in the item data
            Skull s = (Skull) b.getState();
            try {
                GameProfile profile = ProfileFetcher.getProfile(s);
                ItemStack it = event.getCursor();
                SkullMeta sm = (SkullMeta) it.getItemMeta();
                it.setItemMeta(ProfileFetcher.setProfile(sm, profile));
                event.setCursor(it);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
