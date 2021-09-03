package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.inventories.icons.content.SellheadHead;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.managers.EntityDataManager;
import io.github.thatsmusic99.headsplus.managers.SellableHeadsManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellheadCategory extends BaseInventory {

    public SellheadCategory(Player player, HashMap<String, String> context) {
        super(player, context);
    }

    public SellheadCategory() {}
    @Override
    public String getDefaultTitle() {
        return "HeadsPlus Sellhead: {page}/{pages}";
    }

    @Override
    public String getDefaultItems() {
        return "GGGGGGGGGGCCCCCCCGGCCCCCCCGGCCCCCCCGGCCCCCCCG<{[BMN]}>";
    }

    @Override
    public String getId() {
        return "sellhead-category";
    }

    @Override
    public List<Content> transformContents(HashMap<String, String> context, Player player) {
        List<Content> contents = new ArrayList<>();
        switch (context.get("section")) { // ignore
            case "mobs":
                for (String str : SellableHeadsManager.get().getKeys(SellableHeadsManager.SellingType.HUNTING)) {
                    String[] parts = str.substring(5).split(":");
                    if (parts.length < 2) continue;
                    String key = parts[0].toUpperCase() + ";" + (parts[1].equals("default") ? parts[1] : parts[1].toUpperCase());
                    List<EntityDataManager.DroppedHeadInfo> heads = EntityDataManager.getStoredHeads().get(key);
                    if (heads == null || heads.size() == 0) continue;
                    contents.add(new SellheadHead(heads.get(0).forceBuildHead(), str));
                }
                break;
            case "mining": // Guess what
            case "farming":
            case "fishing":
            case "crafting":
                break;
        }
        return contents;
    }
}
