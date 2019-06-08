package io.github.thatsmusic99.headsplus.config.headsx.inventories;

import io.github.thatsmusic99.headsplus.config.headsx.HeadInventory;

public class FavouritesMenu extends HeadInventory {

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Head selector: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return  "GGGGSGGGK" +
                "GHHHHHHHG" +
                "GHHHHHHHG" +
                "GHHHHHHHG" +
                "GHHHHHHHG" +
                "<{[BMN]}>";
    }

    @Override
    public String getDefaultId() {
        return "favourites";
    }

    @Override
    public String getName() {
        return "favourites";
    }
}
