package io.github.thatsmusic99.headsplus.inventories.icons;

import io.github.thatsmusic99.headsplus.inventories.Icon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Content extends Icon {

    public Content(ItemStack itemStack) {
        super(itemStack);
    }

    public Content(Player player) { super(player); }

    public Content() {

    }
}
