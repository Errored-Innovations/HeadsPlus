package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class SellheadMenu extends BaseInventory {

    public SellheadMenu(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Sellhead Menu";
    }

    @Override
    public String getDefaultItems() {
        return  "GGGGGGGGG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GCCCCCCCG" +
                "GGGBXNGGG";
    }

    @Override
    public String getDefaultId() {
        return "sellheadmenu";
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        return null;
    }

}
