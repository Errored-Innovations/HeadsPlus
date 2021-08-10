package io.github.thatsmusic99.headsplus.inventories.icons.list;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import io.github.thatsmusic99.headsplus.inventories.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class ChallengesPinned extends Icon {

    public ChallengesPinned(Player player) {
        super(player);
    }

    public ChallengesPinned() {

    }

    @Override
    public boolean onClick(Player player, InventoryClickEvent event) {
        InventoryManager.getManager(player).open(InventoryManager.InventoryType.CHALLENGES_PINNED, new HashMap<>());
        return true;
    }

    @Override
    public String getId() {
        return "pinned-challenges";
    }

}
