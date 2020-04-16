package io.github.thatsmusic99.headsplus.inventories.list;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.heads.EntityHead;
import io.github.thatsmusic99.headsplus.inventories.BaseInventory;
import io.github.thatsmusic99.headsplus.inventories.icons.Content;
import io.github.thatsmusic99.headsplus.inventories.icons.content.SellheadHead;
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
        return null;
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
        switch (context.get("type")) { // ignore
            case "mobs":
                HashMap<String, List<EntityHead>> heads = HeadsPlus.getInstance().getDeathEvents().getStoredHeads();
                for (String str : heads.keySet()) {
                    if (heads.get(str).size() > 0) {
                        contents.add(new SellheadHead(heads.get(str).get(0).getItemStack(), str));
                    }
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
