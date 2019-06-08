package io.github.thatsmusic99.headsplus.config.headsx.inventories;

import io.github.thatsmusic99.headsplus.config.headsx.HeadInventory;

public class SellheadMenu extends HeadInventory {
    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Sellhead menu";
    }

    @Override
    public String getDefaultItems() {
        return  "GGGGGGGGG" +
                "GHHHHHHHG" +
                "GHHHHHHHG" +
                "GHHHHHHHG" +
                "GHHHHHHHG" +
                "GGGBXNGGG";
    }

    @Override
    public String getDefaultId() {
        return "sellheadmenu";
    }

    @Override
    public String getName() {
        return "sellheadmenu";
    }
}
