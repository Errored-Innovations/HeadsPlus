package io.github.thatsmusic99.headsplus.inventories;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import io.github.thatsmusic99.headsplus.config.customheads.HeadInventory;
import org.bukkit.inventory.Inventory;

public abstract class BaseInventory {

    private static HeadsPlusMessagesManager hpc = HeadsPlus.getInstance().getMessagesConfig();

    public abstract String getDefaultTitle();

    public abstract String getDefaultItems();

    public abstract String getDefaultId();

    public abstract String getName();

    public abstract void gatherContents();

    public abstract Inventory build();


}
