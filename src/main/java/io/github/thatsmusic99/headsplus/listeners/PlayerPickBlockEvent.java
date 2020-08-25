package io.github.thatsmusic99.headsplus.listeners;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.UUID;

import io.github.thatsmusic99.headsplus.HeadsPlus;
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
        Bukkit.getPluginManager().registerEvent(InventoryCreativeEvent.class, this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(InventoryCreativeEvent.class, "InventoryCreativeEvent"), HeadsPlus.getInstance());

        Bukkit.getPluginManager().registerEvent(InventoryOpenEvent.class, new HeadsPlusListener<InventoryOpenEvent>() {
            @Override
            public void onEvent(InventoryOpenEvent event) {
                if (event.getInventory().getType() == InventoryType.CREATIVE) {
                    openInventories.add(event.getPlayer().getUniqueId());
                }
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(InventoryOpenEvent.class, "InventoryOpenEvent (ICE)"), HeadsPlus.getInstance());

        Bukkit.getPluginManager().registerEvent(InventoryCloseEvent.class, new HeadsPlusListener<InventoryCloseEvent>() {
            @Override
            public void onEvent(InventoryCloseEvent event) {
                openInventories.remove(event.getPlayer().getUniqueId());
            }
        }, EventPriority.MONITOR, new HeadsPlusEventExecutor(InventoryCloseEvent.class, "InventoryCloseEvent (ICE)"), HeadsPlus.getInstance());
    }

    public void onEvent(InventoryCreativeEvent event) {
        if (event.getAction() == InventoryAction.PLACE_ALL // this is weird, but ok
                && !openInventories.contains(event.getWhoClicked().getUniqueId())
                && event.getCursor().getItemMeta() instanceof SkullMeta) {
            // Block pick event is basically the same event as picking a block from inventory
            // check to see if they are looking at a skull block
            Block b = event.getWhoClicked().getTargetBlock(null, 6);
            if (b != null && b.getState() instanceof Skull) {
                // fill in the item data
                Skull s = (Skull) b.getState();
                GameProfile profile;
                try {
                    Field profileField;
                    profileField = s.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profile = (GameProfile) profileField.get(s);
                } catch (NoSuchFieldException | IllegalAccessException | SecurityException ex) {
                    throw new RuntimeException("Reflection error while setting head texture", ex);
                }
                ItemStack it = event.getCursor();
                SkullMeta sm = (SkullMeta) it.getItemMeta();
                try {
                    Field profileField;
                    profileField = sm.getClass().getDeclaredField("profile");
                    profileField.setAccessible(true);
                    profileField.set(sm, profile);
                } catch (NoSuchFieldException | IllegalAccessException | SecurityException ex) {
                    throw new RuntimeException("Reflection error while setting head texture", ex);
                }
                it.setItemMeta(sm);
                event.setCursor(it);
            }
        }
    }
}
