package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.MainConfig;
import io.github.thatsmusic99.headsplus.managers.MaskManager;
import io.github.thatsmusic99.headsplus.managers.PersistenceManager;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusEventExecutor;
import io.github.thatsmusic99.headsplus.util.events.HeadsPlusListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class MaskListener extends HeadsPlusListener<InventoryClickEvent> {

    @EventHandler
    public void onEvent(InventoryClickEvent e) {
        ItemStack item;
        boolean shift = e.isShiftClick();
        // If we're shift clicking
        if (shift) {
            // We need to get the current item
            item = e.getCurrentItem();
            ItemStack currHelmet = e.getWhoClicked().getInventory().getHelmet();
            if (!(currHelmet == null || currHelmet.getType().equals(Material.AIR))) return;
        } else {
            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) return;
            item = e.getCursor();
        }

        if (e.getRawSlot() == 5 || (shift && e.getRawSlot() != 5)) {
            checkMask((Player) e.getWhoClicked(), item);
        }
    }

    @Override
    public boolean shouldEnable() {
        return MainConfig.get().getMainFeatures().MASKS;
    }

    @Override
    public void init() {
        Bukkit.getPluginManager().registerEvent(InventoryClickEvent.class,
                this, EventPriority.NORMAL,
                new HeadsPlusEventExecutor(InventoryClickEvent.class, "InventoryClickEvent", this), HeadsPlus.get());

    }

    public static void checkMask(Player player, ItemStack item) {
        if (item == null || !(item.getItemMeta() instanceof SkullMeta)) return;
        String mask = PersistenceManager.get().getMaskType(item);
        if (mask == null) return;
        if (!MaskManager.get().isMaskRegistered(mask)) return;
        MaskManager.MaskInfo maskInfo = MaskManager.get().getMaskInfo(mask);
        maskInfo.run(player);
    }
}
