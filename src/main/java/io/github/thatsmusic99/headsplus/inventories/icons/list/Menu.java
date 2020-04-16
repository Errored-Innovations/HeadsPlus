package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class Menu extends Icon {

    public Menu(Player player) {
        super(player);
    }

    public Menu() {}

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        InventoryManager manager = InventoryManager.getManager(player);
        InventoryManager.InventoryType type = manager.getType();
        switch (manager.getType()) {
            case HEADS_CATEGORY:
            case HEADS_SEARCH:
            case HEADS_FAVORITES:
            case HEADS_MENU:
                type = InventoryManager.InventoryType.HEADS_MENU;
                break;
            case SELLHEAD_MENU:
            case SELLHEAD_CATEGORY:
                type = InventoryManager.InventoryType.SELLHEAD_MENU;
                break;
            case CHALLENGES_PINNED:
            case CHALLENGES_LIST:
            case CHALLENGES_MENU:
                type = InventoryManager.InventoryType.CHALLENGES_MENU;
                break;
        }
        manager.open(type, new HashMap<>());
        return true;
    }

    @Override
    public String getId() {
        return "menu";
    }

    @Override
    public String getDefaultMaterial() {
        return "NETHER_STAR";
    }

    @Override
    public int getDefaultDataValue() {
        return 0;
    }

    @Override
    public String getDefaultDisplayName() {
        return "{msg_inventory.icon.menu}";
    }

    @Override
    public String[] getDefaultLore() {
        return new String[0];
    }
}
