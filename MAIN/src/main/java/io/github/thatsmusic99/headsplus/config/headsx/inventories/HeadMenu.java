package io.github.thatsmusic99.headsplus.config.headsx.inventories;

import io.github.thatsmusic99.headsplus.config.headsx.HeadInventory;

public class HeadMenu extends HeadInventory {

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Head Selector: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return  "FGGGSGGGK" +
                "GLLLLLLLG" +
                "GLLLLLLLG" +
                "GLLLLLLLG" +
                "GLLLLLLLG" +
                "GGGBXNGGG";
    }

    @Override
    public String getDefaultId() {
        return "menu";
    }

    @Override
    public String getName() {
        return "headmenu";
    }
}
